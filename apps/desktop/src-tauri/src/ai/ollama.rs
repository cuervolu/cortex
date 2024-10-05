use crate::error::AppError;
use anyhow::Result;
use log::error;
use ollama_rs::generation::chat::ChatMessage;
use ollama_rs::Ollama;
use std::collections::HashMap;
use std::sync::{Mutex, OnceLock};
use tauri_plugin_shell::ShellExt;

static OLLAMA: OnceLock<Mutex<Option<Ollama>>> = OnceLock::new();
pub static CHAT_CONTEXTS: OnceLock<Mutex<HashMap<String, Vec<ChatMessage>>>> = OnceLock::new();

fn init_ollama() -> Result<(), AppError> {
    let ollama_mutex = OLLAMA.get_or_init(|| Mutex::new(None));
    let mut ollama = ollama_mutex
        .lock()
        .map_err(|_| AppError::OllamaInitializationError)?;
    if ollama.is_none() {
        *ollama = Some(Ollama::default());
    }
    Ok(())
}

pub(crate) fn get_ollama() -> Result<Ollama, AppError> {
    init_ollama()?;
    let ollama_mutex = OLLAMA.get_or_init(|| Mutex::new(None));
    let ollama = ollama_mutex
        .lock()
        .map_err(|_| AppError::OllamaInitializationError)?;
    ollama
        .as_ref()
        .cloned()
        .ok_or(AppError::OllamaInitializationError)
}

#[cfg(target_os = "windows")]
pub(crate) async fn check_ollama_windows(app_handle: &tauri::AppHandle) -> Result<bool, AppError> {
    let shell = app_handle.shell();
    match shell.command("where").args(["ollama"]).output().await {
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
        match shell.command("which").args(["ollama"]).output().await {
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
    match shell.command("which").args(["ollama"]).output().await {
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
