use tauri::{command, Emitter, Window};
use tauri::Manager;
use tauri_plugin_shell::ShellExt;
use std::process::Command;
use std::sync::Mutex;
use futures::StreamExt;
use anyhow::Result;
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
async fn check_ollama_windows(app_handle: &tauri::AppHandle) -> Result<bool, AppError> {
    let shell = app_handle.shell();
    match shell.command("where")
        .args(["ollama"])
        .output()
        .await
    {
        Ok(output) => {
            if output.status.success() && !output.stdout.is_empty() {
                Ok(true)
            } else {
                Ok(false)
            }
        },
        Err(e) => {
            error!("Failed to execute 'where' command: {}", e);
            Err(AppError::CommandExecutionError)
        }
    }
}

#[cfg(target_os = "macos")]
async fn check_ollama_macos(app_handle: &tauri::AppHandle) -> Result<bool, AppError> {
    use std::path::Path;

    if Path::new("/usr/local/bin/ollama").exists() {
        Ok(true)
    } else {
        let shell = app_handle.shell();
        match shell.command("which")
            .args(["ollama"])
            .output()
            .await
        {
            Ok(output) => Ok(output.status.success() && !output.stdout.is_empty()),
            Err(e) => {
                error!("Failed to execute 'which' command: {}", e);
                Err(AppError::CommandExecutionError)
            }
        }
    }
}

#[cfg(target_os = "linux")]
async fn check_ollama_linux(app_handle: &tauri::AppHandle) -> Result<bool, AppError> {
    let shell = app_handle.shell();
    match shell.command("which")
        .args(["ollama"])
        .output()
        .await
    {
        Ok(output) => {
            if output.status.success() && !output.stdout.is_empty() {
                Ok(true)
            } else {
                Ok(false)
            }
        },
        Err(e) => {
            error!("Failed to execute 'which' command: {}", e);
            Err(AppError::CommandExecutionError)
        }
    }
}

#[command]
pub async fn is_ollama_installed(app_handle: tauri::AppHandle) -> Result<bool, AppError> {
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
        check_ollama_linux(&app_handle).await
    }
    #[cfg(not(any(target_os = "windows", target_os = "macos", target_os = "linux")))]
    {
        Err(AppError::UnsupportedOS)
    }
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