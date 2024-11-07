use log::{debug, error, info};
use rand::Rng;
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

    let html_response = r#"
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Autenticación Completada</title>
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                    background: #f5f5f5;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    height: 100vh;
                    margin: 0;
                }
                .container {
                    background: white;
                    padding: 2rem;
                    border-radius: 8px;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    text-align: center;
                    max-width: 90%;
                }
                h1 {
                    color: #2563eb;
                    font-size: 1.5rem;
                    margin-bottom: 1rem;
                }
                p {
                    color: #4b5563;
                    margin: 0;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>¡Autenticación Completada!</h1>
                <p>Puede cerrar esta ventana y volver a la aplicación.</p>
            </div>
        </body>
        </html>
    "#;
    let random_ports: Vec<u16> = (0..5)
        .map(|_| rand::thread_rng().gen_range(49152..65535))
        .collect();

    let config = OauthConfig {
        ports: Some(random_ports),
        response: Some(html_response.into()),
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
    debug!("Handling OAuth callback: {}", url);
    debug!("Code verifier: {}", code_verifier);
    Ok(AuthResponse {
        token: "example_token".to_string(),
    })
}
