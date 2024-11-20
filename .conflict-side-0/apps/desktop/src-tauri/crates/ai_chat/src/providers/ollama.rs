use crate::providers::traits::AIProvider;
use crate::session::session::ExerciseSession;
use crate::session::AIResponse;
use async_trait::async_trait;
use common::API_BASE_URL;
use error::AppError;
use log::{debug, error, info};
use reqwest::Client;
use std::any::Any;
use tauri::{Emitter, WebviewWindow};
use tokio_stream::StreamExt;

pub struct OllamaProvider {
    client: Client,
    window: WebviewWindow,
}

impl OllamaProvider {
    pub fn new(window: WebviewWindow) -> Self {
        Self {
            client: Client::new(),
            window,
        }
    }

    async fn process_stream(&self, response: reqwest::Response) -> Result<AIResponse, AppError> {
        let mut stream = response.bytes_stream();
        let mut full_response = String::new();
        let mut last_chunk = String::new();

        while let Some(chunk_result) = stream.next().await {
            match chunk_result {
                Ok(chunk) => {
                    if let Ok(text) = String::from_utf8(chunk.to_vec()) {
                        if text != last_chunk {
                            debug!("Emitting new chunk: {:?}", text);
                            self.window.emit("ollama-stream", &text).map_err(|e| {
                                error!("Failed to emit stream chunk: {}", e);
                                AppError::TauriError(e)
                            })?;

                            full_response.push_str(&text);
                            last_chunk = text;
                        } else {
                            debug!("Skipping duplicate chunk: {:?}", text);
                        }
                    }
                }
                Err(e) => {
                    error!("Error reading stream chunk: {}", e);
                    return Err(AppError::RequestError(e));
                }
            }
        }

        self.window.emit("ollama-stream-end", ()).map_err(|e| {
            error!("Failed to emit stream end: {}", e);
            AppError::TauriError(e)
        })?;

        Ok(AIResponse {
            text: full_response,
            usage: Default::default(),
        })
    }

    async fn send_message(
        &self,
        session: &ExerciseSession,
        message: String,
    ) -> Result<AIResponse, AppError> {
        info!(
            "Sending message to Ollama. Exercise: {}, Message: {}",
            session.context.exercise_title, message
        );

        let full_message = if let Some(code) = &session.context.editor_content {
            format!(
                "User's code ({}):\n```{}\n{}\n```\n\nUser's question: {}",
                session.context.editor_language.as_deref().unwrap_or("unspecified"),
                session.context.editor_language.as_deref().unwrap_or(""),
                code,
                message
            )
        } else {
            message
        };

        let response = self.client
            .get(format!("{}/chat/ai", API_BASE_URL))
            .header("Accept", "text/plain")
            .query(&[
                ("exerciseSlug", &session.context.exercise_slug),
                ("message", &full_message),
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

        self.process_stream(response).await
    }
}

#[async_trait]
impl AIProvider for OllamaProvider {
    fn provider_name(&self) -> &'static str {
        "ollama"
    }

    fn requires_api_key(&self) -> bool {
        false
    }

    async fn send_message(
        &self,
        session: &ExerciseSession,
        message: String,
    ) -> Result<AIResponse, AppError> {
        self.send_message(session, message).await
    }

    fn as_any(&self) -> &dyn Any {
        self
    }
}