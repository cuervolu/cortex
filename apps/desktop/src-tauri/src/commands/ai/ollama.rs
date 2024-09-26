use std::process::Command;
use std::sync::Mutex;
use futures::StreamExt;
use tauri::{command, Emitter};
use anyhow::{Result, Context};
use lazy_static::lazy_static;
use log::{error, info};
use ollama_rs::Ollama;
use ollama_rs::generation::completion::request::GenerationRequest;
use crate::error::AppError;

lazy_static! {
    static ref OLLAMA: Mutex<Option<Ollama>> = Mutex::new(None);
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
    window: tauri::Window,
) -> Result<(), AppError> {
    info!("Attempting to send prompt to Ollama. Model: {}, Prompt: {}", model, prompt);

    let ollama = get_ollama()?;

    let request = GenerationRequest::new(model, prompt);

    let mut stream = ollama.generate_stream(request).await.map_err(|e| {
        error!("Error generating response stream from Ollama: {:?}", e);
        AppError::AIServiceError(format!("Ollama error: {}", e))
    })?;

    while let Some(res) = stream.next().await {
        match res {
            Ok(responses) => {
                for response in responses {
                    window.emit("ollama-response", &response.response).map_err(|e| {
                        error!("Error emitting Ollama response: {:?}", e);
                        AppError::TauriError(e)
                    })?;
                }
            }
            Err(e) => {
                error!("Error in Ollama response stream: {:?}", e);
                return Err(AppError::AIServiceError(format!("Ollama stream error: {}", e)));
            }
        }
    }

    Ok(())
}