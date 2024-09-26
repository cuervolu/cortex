use std::process::Command;
use std::sync::Mutex;
use futures::StreamExt;
use tauri::{command, Emitter, Window};
use anyhow::{Result, Context};
use lazy_static::lazy_static;
use log::{error, info};
use ollama_rs::Ollama;
use ollama_rs::generation::chat::{ChatMessage};
use std::collections::HashMap;
use ollama_rs::generation::chat::request::ChatMessageRequest;
use crate::error::AppError;

lazy_static! {
    static ref OLLAMA: Mutex<Option<Ollama>> = Mutex::new(None);
    static ref CHAT_CONTEXTS: Mutex<HashMap<String, Vec<ChatMessage>>> = Mutex::new(HashMap::new());
}

fn init_ollama() -> Result<(), AppError> {
    let mut ollama = OLLAMA.lock().map_err(|_| AppError::OllamaInitializationError)?;
    if ollama.is_none() {
        *ollama = Some(Ollama::default());
    }
    Ok(())
}

fn get_ollama() -> Result<Ollama, AppError> {
    init_ollama()?;
    let ollama = OLLAMA.lock().map_err(|_| AppError::OllamaInitializationError)?;
    ollama.as_ref().cloned().ok_or(AppError::OllamaInitializationError)
}

#[cfg(target_os = "windows")]
fn check_ollama_windows() -> Result<bool> {
    Command::new("where")
        .arg("ollama")
        .output()
        .context("Failed to execute 'where' command")?
        .status
        .success()
        .then_some(true)
        .ok_or(AppError::OllamaNotFound.into())
}

#[cfg(target_os = "macos")]
fn check_ollama_macos() -> Result<bool> {
    std::path::Path::new("/usr/local/bin/ollama")
        .exists()
        .then_some(true)
        .ok_or(AppError::OllamaNotFound.into())
}

#[cfg(target_os = "linux")]
fn check_ollama_linux() -> Result<bool> {
    Command::new("which")
        .arg("ollama")
        .output()
        .context("Failed to execute 'which' command")?
        .status
        .success()
        .then_some(true)
        .ok_or(AppError::OllamaNotFound.into())
}

#[command]
pub fn is_ollama_installed() -> Result<bool, AppError> {
    #[cfg(target_os = "windows")]
    return check_ollama_windows().map_err(|e| e.downcast().unwrap_or(AppError::CommandExecutionError));
    #[cfg(target_os = "macos")]
    return check_ollama_macos().map_err(|e| e.downcast().unwrap_or(AppError::CommandExecutionError));
    #[cfg(target_os = "linux")]
    return check_ollama_linux().map_err(|e| e.downcast().unwrap_or(AppError::CommandExecutionError));
    #[cfg(not(any(target_os = "windows", target_os = "macos", target_os = "linux")))]
    Err(AppError::UnsupportedOS)
}

#[command]
pub async fn send_prompt_to_ollama(
    model: String,
    prompt: String,
    user_id: String,
    window: Window,
) -> Result<(), AppError> {
    info!("Attempting to send prompt to Ollama. Model: {}, Prompt: {}, User ID: {}", model, prompt, user_id);
    let ollama = get_ollama()?;

    // Get chat context for this user
    let context = {
        let mut contexts = CHAT_CONTEXTS.lock().map_err(|_| AppError::ContextLockError)?;
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

    //  Update context with full AI response
    {
        let mut contexts = CHAT_CONTEXTS.lock().map_err(|_| AppError::ContextLockError)?;
        let context = contexts.get_mut(&user_id).ok_or(AppError::ContextNotFound)?;
        context.push(ChatMessage::assistant(full_response));

        // Limit context size if necessary
        if context.len() > 10 {
            *context = context.drain(context.len() - 10..).collect();
        }
    }

    Ok(())
}