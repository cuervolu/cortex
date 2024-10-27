pub mod commands;
pub mod anthropic;

use futures::StreamExt;
use log::error;
use tauri::{Emitter, Window};
use common::{API_BASE_URL, CLIENT};
use error::AppError;

async fn send_chat_message(exercise_slug: &str, message: &str, window: &Window) -> Result<(), AppError> {
    let response = CLIENT
        .get(format!("{}/chat/ai", API_BASE_URL))
        .query(&[
            ("exerciseSlug", exercise_slug),
            ("message", message),
        ])
        .send()
        .await
        .map_err(|e| {
            error!("Failed to send chat request: {}", e);
            AppError::RequestError(e)
        })?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        error!("API request failed: {}", error_msg);
        return Err(AppError::AIServiceError(error_msg));
    }

    let mut stream = response.bytes_stream();

    while let Some(chunk_result) = stream.next().await {
        match chunk_result {
            Ok(chunk) => {
                if let Ok(text) = String::from_utf8(chunk.to_vec()) {
                    window.emit("ollama-response", text).map_err(|e| {
                        error!("Failed to emit chat response chunk: {}", e);
                        AppError::TauriError(e)
                    })?;
                }
            }
            Err(e) => {
                error!("Error reading stream chunk: {}", e);
                return Err(AppError::RequestError(e));
            }
        }
    }

    window.emit("ollama-response-end", ()).map_err(|e| {
        error!("Failed to emit response end: {}", e);
        AppError::TauriError(e)
    })?;

    Ok(())
}