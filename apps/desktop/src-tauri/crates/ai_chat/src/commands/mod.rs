use log::info;
use tauri::Window;
use error::AppError;
use crate::send_chat_message;

#[tauri::command]
pub async fn send_chat_prompt(
    exercise_slug: String,
    message: String,
    window: Window,
) -> Result<(), AppError> {
    info!(
        "Received chat prompt request. Exercise: {}, Message: {}",
        exercise_slug, message
    );

    send_chat_message(&exercise_slug, &message, &window).await
}