use futures::StreamExt;
use log::{error, info};
use ollama_rs::generation::chat::ChatMessage;
use ollama_rs::generation::chat::request::ChatMessageRequest;
use tauri::{AppHandle, Emitter, Window, Wry};
use crate::ai::ollama::check_ollama_linux;
use crate::ai::ollama_models::{OllamaModel, update_ollama_models};
use crate::error::AppError;

#[tauri::command]
pub async fn get_ollama_models(app_handle: AppHandle<Wry>) -> Result<Vec<OllamaModel>, AppError> {
    crate::ai::ollama_models::get_ollama_models(&app_handle).await
}

#[tauri::command]
pub async fn refresh_ollama_models(app_handle: AppHandle<Wry>) -> Result<(), AppError> {
    update_ollama_models(&app_handle, true).await
}

#[tauri::command]
pub async fn is_ollama_installed(app_handle: AppHandle) -> Result<bool, AppError> {
    #[cfg(target_os = "windows")]
    {
        check_ollama_windows(&app_handle).await
    }
    #[cfg(target_os = "macos")]
    {
        check_ollama_macos(&app_handle).await
    }
    #[cfg(target_os = "linux")]
    {
        return check_ollama_linux(&app_handle).await;
    }
    #[cfg(not(any(target_os = "windows", target_os = "macos", target_os = "linux")))]
    {
        Err(AppError::UnsupportedOS)
    }
}

#[tauri::command]
pub async fn send_prompt_to_ollama(
    model: String,
    prompt: String,
    user_id: String,
    window: Window,
) -> Result<(), AppError> {
    info!("Attempting to send prompt to Ollama. Model: {}, Prompt: {}, User ID: {}", model, prompt, user_id);
    let ollama = crate::ai::ollama::get_ollama()?;

    // Get chat context for this user
    let context = {
        let mut contexts = crate::ai::ollama::CHAT_CONTEXTS.lock().map_err(|_| AppError::ContextLockError)?;
        let context = contexts.entry(user_id.clone()).or_insert_with(Vec::new);
        context.push(ChatMessage::user(prompt.clone()));
        context.clone()
    };

    // Create chat request with full context
    let request = ChatMessageRequest::new(model, context);

    let mut stream = ollama.send_chat_messages_stream(request).await.map_err(|e| {
        error!("Error generating response stream from Ollama: {:?}", e);
        AppError::AIServiceError(format!("Ollama error: {}", e))
    })?;

    let mut full_response = String::new();

    while let Some(res) = stream.next().await {
        match res {
            Ok(response) => {
                if let Some(message) = response.message {
                    full_response.push_str(&message.content);
                    window.emit("ollama-response", &message.content).map_err(|e| {
                        error!("Error emitting Ollama response: {:?}", e);
                        AppError::TauriError(e)
                    })?;
                } else {
                    error!("Received empty message from Ollama");
                }
            }
            Err(e) => {
                error!("Error in Ollama response stream: {:?}", e);
                return Err(AppError::AIServiceError(format!("Ollama stream error: {:?}", e)));
            }
        }
    }

    window.emit("ollama-response-end", ())?; // Broadcast a final event to indicate that the stream has ended

    // Update context with full AI response
    {
        let mut contexts = crate::ai::ollama::CHAT_CONTEXTS.lock().map_err(|_| AppError::ContextLockError)?;
        let context = contexts.get_mut(&user_id).ok_or(AppError::ContextNotFound)?;
        context.push(ChatMessage::assistant(full_response));

        // Limit context size if necessary
        if context.len() > 10 {
            *context = context.drain(context.len() - 10..).collect();
        }
    }

    Ok(())
}

#[tauri::command]
pub async fn list_local_models() -> anyhow::Result<Vec<String>, AppError> {
    let ollama = crate::ai::ollama::get_ollama()?;
    let models = ollama.list_local_models().await.map_err(|e| {
        error!("Error listing local models: {:?}", e);
        AppError::AIServiceError(format!("Ollama error: {}", e))
    })?;
    Ok(models.into_iter().map(|m| m.name).collect())
}