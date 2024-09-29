use futures::StreamExt;
use log::{error, info};
use ollama_rs::generation::chat::request::ChatMessageRequest;
use ollama_rs::generation::chat::ChatMessage;
use tauri::{AppHandle, Emitter, Manager, Window, Wry};
use tauri_plugin_shell::process::CommandEvent;
use tauri_plugin_shell::ShellExt;

#[cfg(target_os = "linux")]
use crate::ai::ollama::check_ollama_linux;

#[cfg(target_os = "macos")]
use crate::ai::ollama::check_ollama_macos;

#[cfg(target_os = "windows")]
use crate::ai::ollama::check_ollama_windows;

use crate::ai::ollama_models::{force_update_ollama_models, update_ollama_models, OllamaModel};
use crate::ai::{OllamaModelDetails, OllamaModelInfo};
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
    info!(
        "Attempting to send prompt to Ollama. Model: {}, Prompt: {}, User ID: {}",
        model, prompt, user_id
    );
    let ollama = crate::ai::ollama::get_ollama()?;

    // Get chat context for this user
    let context = {
        let mut contexts = crate::ai::ollama::CHAT_CONTEXTS
            .lock()
            .map_err(|_| AppError::ContextLockError)?;
        let context = contexts.entry(user_id.clone()).or_insert_with(Vec::new);
        context.push(ChatMessage::user(prompt.clone()));
        context.clone()
    };

    // Create chat request with full context
    let request = ChatMessageRequest::new(model, context);

    let mut stream = ollama
        .send_chat_messages_stream(request)
        .await
        .map_err(|e| {
            error!("Error generating response stream from Ollama: {:?}", e);
            AppError::AIServiceError(format!("Ollama error: {}", e))
        })?;

    let mut full_response = String::new();

    while let Some(res) = stream.next().await {
        match res {
            Ok(response) => {
                if let Some(message) = response.message {
                    full_response.push_str(&message.content);
                    window
                        .emit("ollama-response", &message.content)
                        .map_err(|e| {
                            error!("Error emitting Ollama response: {:?}", e);
                            AppError::TauriError(e)
                        })?;
                } else {
                    error!("Received empty message from Ollama");
                }
            }
            Err(e) => {
                error!("Error in Ollama response stream: {:?}", e);
                return Err(AppError::AIServiceError(format!(
                    "Ollama stream error: {:?}",
                    e
                )));
            }
        }
    }

    window.emit("ollama-response-end", ())?; // Broadcast a final event to indicate that the stream has ended

    // Update context with full AI response
    {
        let mut contexts = crate::ai::ollama::CHAT_CONTEXTS
            .lock()
            .map_err(|_| AppError::ContextLockError)?;
        let context = contexts
            .get_mut(&user_id)
            .ok_or(AppError::ContextNotFound)?;
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

#[tauri::command]
pub async fn list_ollama_models(app_handle: AppHandle) -> anyhow::Result<Vec<OllamaModelInfo>, AppError> {
    let shell = app_handle.shell();
    let output = shell.command("ollama")
        .args(["list"])
        .output()
        .await
        .map_err(|e| AppError::CommandExecutionError(e.to_string()))?;

    let output_str = String::from_utf8_lossy(&output.stdout);
    let lines: Vec<&str> = output_str.lines().skip(1).collect(); // Skip header

    let models: Vec<OllamaModelInfo> = lines
        .iter()
        .filter_map(|line| {
            let parts: Vec<&str> = line.split_whitespace().collect();
            if parts.len() >= 4 {
                Some(OllamaModelInfo {
                    name: parts[0].to_string(),
                    id: parts[1].to_string(),
                    size: parts[2].to_string(),
                    modified: parts[3..].join(" "),
                })
            } else {
                None
            }
        })
        .collect();

    Ok(models)
}

#[tauri::command]
pub async fn show_ollama_model(app_handle: AppHandle, model_name: String) -> Result<OllamaModelDetails, AppError> {
    let shell = app_handle.shell();
    let output = shell.command("ollama")
        .args(["show", &model_name])
        .output()
        .await
        .map_err(|e| AppError::CommandExecutionError(e.to_string()))?;

    let output_str = String::from_utf8_lossy(&output.stdout);
    let lines: Vec<&str> = output_str.lines().collect();

    let mut details = OllamaModelDetails {
        architecture: String::new(),
        parameters: String::new(),
        context_length: String::new(),
        embedding_length: String::new(),
        quantization: String::new(),
        license: String::new(),
    };

    let mut in_license = false;
    for line in lines {
        if line.contains("License") {
            in_license = true;
            continue;
        }
        if in_license {
            details.license.push_str(line);
            details.license.push('\n');
        } else if let Some((key, value)) = line.split_once(':') {
            let key = key.trim();
            let value = value.trim();
            match key {
                "architecture" => details.architecture = value.to_string(),
                "parameters" => details.parameters = value.to_string(),
                "context length" => details.context_length = value.to_string(),
                "embedding length" => details.embedding_length = value.to_string(),
                "quantization" => details.quantization = value.to_string(),
                _ => {}
            }
        }
    }

    Ok(details)
}

#[tauri::command]
pub async fn pull_ollama_model(app_handle: AppHandle, model_name: String) -> Result<(), AppError> {
    info!("Starting to pull Ollama model: {}", model_name);
    let shell = app_handle.shell();
    let (mut rx, _child) = shell.command("ollama")
        .args(["pull", &model_name])
        .spawn()
        .map_err(|e| {
            error!("Failed to spawn 'ollama pull' command: {}", e);
            AppError::CommandExecutionError(e.to_string())
        })?;

    let window = app_handle.get_webview_window("main").ok_or_else(|| {
        error!("Failed to get main window");
        AppError::WindowNotFound
    })?;

    while let Some(event) = rx.recv().await {
        match event {
            CommandEvent::Stdout(line) => {
                info!("Ollama pull progress: {:?}", line);
                window.emit("ollama-pull-progress", &line)
                    .map_err(|e| {
                        error!("Failed to emit ollama-pull-progress event: {}", e);
                        AppError::TauriError(e)
                    })?;
            }
            CommandEvent::Stderr(line) => {
                error!("Ollama pull error:  {:?}", line);
                window.emit("ollama-pull-error", &line)
                    .map_err(|e| {
                        error!("Failed to emit ollama-pull-error event: {}", e);
                        AppError::TauriError(e)
                    })?;
                break;
            }
            CommandEvent::Error(err) => {
                error!("Ollama pull command error: {}", err);
                return Err(AppError::CommandExecutionError(err));
            }
            CommandEvent::Terminated(status) => {
                info!("Ollama pull command terminated with status: {:?}", status);
                break;
            }
            _ => {
                info!("Received unexpected event from ollama pull command");
            }
        }
    }

    info!("Finished pulling Ollama model: {}", model_name);
    Ok(())
}

#[tauri::command]
pub async fn forced_update(app_handle: AppHandle) -> Result<(), AppError> {
    force_update_ollama_models(&app_handle).await
}