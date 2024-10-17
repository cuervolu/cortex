use common::state::{AppState, User};
use error::AppError;
use tauri::State;

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
    state.user.lock().map_err(|_| AppError::ContextLockError)
        .map(|user| user.clone())
}

#[tauri::command]
pub fn get_token(state: State<'_, AppState>) -> Result<Option<String>, AppError> {
    state.token.lock().map_err(|_| AppError::ContextLockError)
        .map(|token| token.clone())
}

#[tauri::command]
pub fn clear_user(state: State<'_, AppState>) -> Result<(), AppError> {
    let mut user_lock = state.user.lock().map_err(|_| AppError::ContextLockError)?;
    let mut token_lock = state.token.lock().map_err(|_| AppError::ContextLockError)?;
    *user_lock = None;
    *token_lock = None;
    Ok(())
}