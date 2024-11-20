use crate::session::context::ExerciseContext;
use crate::session::manager::AISessionManager;
use error::AppError;
use std::sync::Arc;
use tauri::State;

#[tauri::command]
pub async fn start_exercise_session(
    context: ExerciseContext,
    state: State<'_, Arc<AISessionManager>>,
) -> Result<(), AppError> {
    let manager = state.as_ref();
    manager.start_session(context).await
}

#[tauri::command]
pub async fn send_message(
    exercise_id: String,
    message: String,
    state: State<'_, Arc<AISessionManager>>,
) -> Result<(), AppError> {
    let manager = state.as_ref();
    manager.send_message(&exercise_id, &message).await
}

#[tauri::command]
pub async fn end_exercise_session(
    exercise_id: String,
    state: State<'_, Arc<AISessionManager>>,
) -> Result<(), AppError> {
    let manager = state.as_ref();
    manager.end_session(&exercise_id).await
}

#[tauri::command]
pub async fn set_provider(
    provider_name: String,
    window: tauri::WebviewWindow,
    state: State<'_, Arc<AISessionManager>>,
) -> Result<(), AppError> {
    let manager = state.as_ref();
    manager.set_provider(&provider_name, window).await
}

#[tauri::command]
pub async fn set_provider_api_key(
    provider_name: String,
    api_key: Option<String>,
    user_id: i32,  // Agregamos el user_id
    state: State<'_, Arc<AISessionManager>>,
) -> Result<(), AppError> {
    let manager = state.as_ref();
    manager.set_provider_api_key(&provider_name, api_key, user_id).await
}
#[tauri::command]
pub async fn remove_provider_api_key(
    state: State<'_, Arc<AISessionManager>>,
) -> Result<(), AppError> {
    let manager = state.as_ref();
    manager.remove_provider_api_key().await
}

#[tauri::command]
pub async fn remove_all_api_keys(
    state: State<'_, Arc<AISessionManager>>,
) -> Result<(), AppError> {
    let manager = state.as_ref();
    manager.remove_all_api_keys().await
}

#[tauri::command]
pub async fn get_provider_api_key(
    provider_name: String,
    state: State<'_, Arc<AISessionManager>>,
) -> Result<String, AppError> {
    let manager = state.as_ref();
    manager.get_provider_api_key(&provider_name).await
}
