use crate::providers::traits::AIProvider;
use crate::session::session::ExerciseSession;
use crate::session::AIResponse;
use async_trait::async_trait;
use error::AppError;
use futures::StreamExt;
use log::{debug, error};
use reqwest::header::{self, HeaderMap, HeaderValue};
use serde::Serialize;
use std::any::Any;
use std::sync::Arc;
use tauri::{Emitter, WebviewWindow};
use tokio::sync::RwLock;

const ANTHROPIC_API_URL: &str = "https://api.anthropic.com/v1/messages";
const ANTHROPIC_VERSION: &str = "2023-06-01";

#[derive(Debug, Serialize)]
struct Message {
    role: String,
    content: String,
}

#[derive(Debug, Serialize)]
struct ChatRequest {
    model: String,
    max_tokens: u32,
    messages: Vec<Message>,
    #[serde(skip_serializing_if = "Option::is_none")]
    stream: Option<bool>,
}

pub struct ClaudeProvider {
    headers: Arc<RwLock<HeaderMap>>,
    window: WebviewWindow,
}

impl ClaudeProvider {
    pub fn new(window: WebviewWindow) -> Result<Self, AppError> {
        let mut headers = HeaderMap::new();
        headers.insert("anthropic-version", HeaderValue::from_static(ANTHROPIC_VERSION));
        headers.insert(header::CONTENT_TYPE, HeaderValue::from_static("application/json"));

        Ok(Self {
            headers: Arc::new(RwLock::new(headers)),
            window,
        })
    }

    pub async fn set_api_key(&self, api_key: String) -> Result<(), AppError> {
        let api_key_header = HeaderValue::from_str(&api_key)
            .map_err(|e| AppError::AIServiceError(format!("Invalid API key header: {}", e)))?;

        let mut headers = self.headers.write().await;
        headers.insert("x-api-key", api_key_header);
        Ok(())
    }

    async fn process_stream_chunk(&self, chunk: &[u8]) -> Result<Option<String>, AppError> {
        if let Ok(text) = String::from_utf8(chunk.to_vec()) {
            if let Some(data) = text.strip_prefix("event: content_block_delta\ndata: ") {
                if let Ok(json) = serde_json::from_str::<serde_json::Value>(data) {
                    if let Some(delta) = json.get("delta") {
                        if let Some(text_delta) = delta.get("text") {
                            if let Some(content) = text_delta.as_str() {
                                self.window.emit("claude-stream", content).map_err(|e| {
                                    error!("Failed to emit stream chunk: {}", e);
                                    AppError::TauriError(e)
                                })?;
                                return Ok(Some(content.to_string()));
                            }
                        }
                    }
                }
            }
        }
        Ok(None)
    }

    async fn handle_stream(&self, response: reqwest::Response) -> Result<String, AppError> {
        if !response.status().is_success() {
            let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
            error!("Stream error: {}", error_msg);
            return Err(AppError::AIServiceError(error_msg));
        }

        let mut stream = response.bytes_stream();
        let mut buffer = Vec::new();
        let mut full_response = String::new();

        while let Some(chunk_result) = stream.next().await {
            match chunk_result {
                Ok(chunk) => {
                    buffer.extend_from_slice(&chunk);
                    debug!("Stream chunk: {:?}", chunk);
                    let mut start = 0;
                    while let Some(pos) = buffer[start..].windows(2).position(|w| w == b"\n\n") {
                        let pos = start + pos;
                        let line = &buffer[start..pos];

                        if let Some(content) = self.process_stream_chunk(line).await? {
                            full_response.push_str(&content);
                        }

                        start = pos + 2;
                    }

                    if start > 0 {
                        buffer.drain(..start);
                    }
                }
                Err(e) => {
                    error!("Error reading stream chunk: {}", e);
                    return Err(AppError::RequestError(e));
                }
            }
        }
        if !buffer.is_empty() {
            if let Some(content) = self.process_stream_chunk(&buffer).await? {
                full_response.push_str(&content);
            }
        }

        self.window.emit("claude-stream-end", ()).map_err(|e| {
            error!("Failed to emit stream end: {}", e);
            AppError::TauriError(e)
        })?;

        Ok(full_response)
    }

    fn build_system_prompt(&self, session: &ExerciseSession) -> String {
        let mut prompt = String::new();
        prompt.push_str(
            "Eres CORTEX-IA, un asistente educativo especializado en una plataforma de ejercicios de programación. \
            Tu objetivo principal es guiar a los estudiantes en su proceso de aprendizaje, siguiendo estas directrices:\n\n"
        );
        prompt.push_str("\
            1. NUNCA proporciones soluciones completas o código que resuelva directamente el ejercicio.\n\
            2. Enfócate en dar pistas constructivas y hacer preguntas que estimulen el pensamiento crítico.\n\
            3. Adapta tu nivel de ayuda según el progreso y comprensión del estudiante.\n\
            4. Si notas errores en el código, señálalos de manera constructiva y sugiere áreas de mejora.\n\
            5. Mantén un tono amigable y motivador, celebrando los avances del estudiante.\n\n"
        );

        let context = &session.context;
        prompt.push_str(&format!(
            "Contexto del ejercicio:\n\
            - Título: {}\n\
            - Instrucciones: {}\n",
            context.exercise_title, context.exercise_instructions
        ));

        if let Some(hints) = &context.exercise_hints {
            prompt.push_str(&format!("- Pistas disponibles: {}\n", hints));
        }
        prompt
            .push_str(&format!("\nEstás interactuando con el estudiante: {}\n", context.username));

        if let Some(content) = &context.editor_content {
            prompt.push_str("\nCódigo actual del estudiante ");
            if let Some(lang) = &context.editor_language {
                prompt.push_str(&format!("en {}:\n", lang));
            } else {
                prompt.push_str(":\n");
            }
            prompt.push_str(&format!("```\n{}\n```\n", content));
        }

        prompt.push_str(
            "\nRecuerda:\n\
            - Guía sin resolver\n\
            - Fomenta el pensamiento independiente\n\
            - Celebra los logros\n\
            - Mantén un tono motivador\n",
        );

        prompt
    }
}

#[async_trait]
impl AIProvider for ClaudeProvider {
    fn provider_name(&self) -> &'static str {
        "claude"
    }

    fn requires_api_key(&self) -> bool {
        true
    }

    async fn send_message(
        &self,
        session: &ExerciseSession,
        message: String,
    ) -> Result<AIResponse, AppError> {
        let headers = self.headers.read().await;
        debug!("Headers: {:?}", *headers);
        // Verificar que la API key esté configurada
        if !headers.contains_key("x-api-key") {
            return Err(AppError::AIServiceError("API key not configured".into()));
        }

        debug!("Sending message to Claude");

        let system_prompt = self.build_system_prompt(session);
        let mut chat_messages = vec![Message {
            role: "assistant".to_string(),
            content: system_prompt,
        }];

        // Agregar mensajes previos
        chat_messages.extend(session.messages.iter().map(|msg| Message {
            role: msg.role.clone(),
            content: msg.content.clone(),
        }));

        // Agregar mensaje actual
        chat_messages.push(Message {
            role: "user".to_string(),
            content: message,
        });

        let request = ChatRequest {
            model: "claude-3-sonnet-20240229".to_string(),
            max_tokens: 1024,
            messages: chat_messages,
            stream: Some(true),
        };

        let client = reqwest::Client::new();
        let response = client
            .post(ANTHROPIC_API_URL)
            .headers(headers.clone())
            .json(&request)
            .send()
            .await
            .map_err(AppError::RequestError)?;

        let response_text = self.handle_stream(response).await?;

        Ok(AIResponse {
            text: response_text,
            usage: Default::default(),
        })
    }

    fn as_any(&self) -> &dyn Any {
        self
    }
}
