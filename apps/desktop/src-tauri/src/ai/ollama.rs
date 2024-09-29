use tauri_plugin_shell::ShellExt;
use std::sync::Mutex;
use anyhow::Result;
use lazy_static::lazy_static;
use log::error;
use ollama_rs::Ollama;
use ollama_rs::generation::chat::{ChatMessage};
use std::collections::HashMap;
use crate::error::AppError;

lazy_static! {
    static ref OLLAMA: Mutex<Option<Ollama>> = Mutex::new(None);
    pub static ref CHAT_CONTEXTS: Mutex<HashMap<String, Vec<ChatMessage>>> = Mutex::new(HashMap::new());
}

fn init_ollama() -> Result<(), AppError> {
    let mut ollama = OLLAMA.lock().map_err(|_| AppError::OllamaInitializationError)?;
    if ollama.is_none() {
        *ollama = Some(Ollama::default());
    }
    Ok(())
}

pub(crate) fn get_ollama() -> Result<Ollama, AppError> {
    init_ollama()?;
    let ollama = OLLAMA.lock().map_err(|_| AppError::OllamaInitializationError)?;
    ollama.as_ref().cloned().ok_or(AppError::OllamaInitializationError)
}

#[cfg(target_os = "windows")]
pub(crate) async fn check_ollama_windows(app_handle: &tauri::AppHandle) -> Result<bool, AppError> {
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
        }
        Err(e) => {
            error!("Failed to execute 'where' command: {}", e);
            Err(AppError::CommandExecutionError(e.to_string()))
        }
    }
}

#[cfg(target_os = "macos")]
pub(crate) async fn check_ollama_macos(app_handle: &tauri::AppHandle) -> Result<bool, AppError> {
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
pub(crate) async fn check_ollama_linux(app_handle: &tauri::AppHandle) -> Result<bool, AppError> {
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
        }
        Err(e) => {
            error!("Failed to execute 'which' command: {}", e);
            Err(AppError::CommandExecutionError(e.to_string()))
        }
    }
}

