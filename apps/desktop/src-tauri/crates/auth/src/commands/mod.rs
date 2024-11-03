use log::{debug, error, info};
use crate::{generate_code_challenge, generate_random_string, AuthResponse, OAuthConfig};
use common::state::{AppState, User};
use error::AppError;
use tauri::{Emitter, Manager, State};
use tauri_plugin_oauth::{cancel, start_with_config, OauthConfig};

#[tauri::command]
pub fn set_user(user: User, token: String, state: State<'_, AppState>) -> Result<(), AppError> {
    let mut user_lock = state.user.lock().map_err(|_| AppError::ContextLockError)?;
    let mut token_lock = state.token.lock().map_err(|_| AppError::ContextLockError)?;
    *user_lock = Some(user);
    *token_lock = Some(token);
    Ok(())
}

#[tauri::command]
pub fn get_user(state: State<'_, AppState>) -> Result<Option<User>, AppError> {
    state.user.lock().map_err(|_| AppError::ContextLockError).map(|user| user.clone())
}

#[tauri::command]
pub fn get_token(state: State<'_, AppState>) -> Result<Option<String>, AppError> {
    state.token.lock().map_err(|_| AppError::ContextLockError).map(|token| token.clone())
}

#[tauri::command]
pub fn clear_user(state: State<'_, AppState>) -> Result<(), AppError> {
    let mut user_lock = state.user.lock().map_err(|_| AppError::ContextLockError)?;
    let mut token_lock = state.token.lock().map_err(|_| AppError::ContextLockError)?;
    *user_lock = None;
    *token_lock = None;
    Ok(())
}

#[tauri::command]
pub async fn start_oauth_flow(
    app: tauri::AppHandle,
    provider: String,
) -> Result<OAuthConfig, AppError> {
    info!("Starting OAuth flow for provider: {}", provider);

    let code_verifier = generate_random_string(128);
    let code_challenge = generate_code_challenge(&code_verifier);

    debug!("Generated PKCE challenge");
    
    let config = OauthConfig {
        ports: Some(vec![12345, 12346, 12347]), 
        response: Some("Autenticación completada. Puede cerrar esta ventana.".into()),
    };

    let window = app.get_webview_window("main").ok_or_else(|| {
        error!("Failed to get main window");
        AppError::WindowNotFound
    })?;
    
    match start_with_config(config, move |url| {
        debug!("Received OAuth callback URL");
        if let Err(e) = window.emit("oauth://url", url) {
            error!("Failed to emit oauth URL: {}", e);
        }
    }) {
        Ok(port) => {
            info!("OAuth server started on port {}", port);
            Ok(OAuthConfig {
                provider,
                code_verifier,
                code_challenge,
                port,
            })
        }
        Err(e) => {
            error!("Failed to start OAuth server: {}", e);
            Err(AppError::OAuthError(e.to_string()))
        }
    }
}

#[tauri::command]
pub fn cancel_oauth_flow(port: u16) -> Result<(), AppError> {
    cancel(port).map_err(|e| AppError::OAuthError(e.to_string()))
}

#[tauri::command]
pub async fn handle_oauth_callback(
    url: String,
    code_verifier: String,
) -> Result<AuthResponse, AppError> {
    Ok(AuthResponse {
        token: "example_token".to_string(),
    })
}
