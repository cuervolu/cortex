use crate::providers::traits::AIProvider;
use crate::session::session::ExerciseSession;
use crate::session::AIResponse;
use async_trait::async_trait;
use error::AppError;
use futures::StreamExt;
use log::{debug, error};
use reqwest::header::{self};
use serde::{Deserialize, Serialize};
use std::any::Any;
use std::sync::Arc;
use tauri::{Emitter, WebviewWindow};
use tokio::sync::RwLock;

const GEMINI_API_URL: &str =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent";

#[derive(Debug, Deserialize)]
struct GeminiError {
    error: GeminiErrorDetails,
}

#[derive(Debug, Deserialize)]
struct GeminiErrorDetails {
    code: i32,
    message: String,
    status: String,
}

#[derive(Debug, Serialize)]
struct GeminiRequest {
    model: String,
    contents: Vec<Content>,
}
#[allow(dead_code)]
#[derive(Debug, Deserialize)]
struct StreamResponse {
    candidates: Vec<Candidate>,
    #[serde(rename = "usageMetadata")]
    usage_metadata: Option<UsageMetadata>,
    #[serde(rename = "modelVersion")]
    model_version: Option<String>,
}
#[allow(dead_code)]
#[derive(Debug, Deserialize)]
struct Candidate {
    content: Content,
    #[serde(default)]
    index: i32,
    #[serde(rename = "safetyRatings", default)]
    safety_ratings: Vec<SafetyRating>,
    #[serde(rename = "finishReason", default)]
    finish_reason: Option<String>,
}
#[derive(Debug, Serialize, Deserialize)]
struct Content {
    parts: Vec<Part>,
    role: String,
}

#[derive(Debug, Serialize, Deserialize)]
struct Part {
    text: String,
}
#[allow(dead_code)]
#[derive(Debug, Deserialize)]
struct SafetyRating {
    category: String,
    probability: String,
}
#[allow(dead_code)]
#[derive(Debug, Deserialize, Clone)]
struct UsageMetadata {
    #[serde(rename = "promptTokenCount")]
    prompt_token_count: u32,
    #[serde(rename = "candidatesTokenCount", default)]
    candidates_token_count: u32,
    #[serde(rename = "totalTokenCount")]
    total_token_count: u32,
}

pub struct GeminiProvider {
    api_key: Arc<RwLock<Option<String>>>,
    window: WebviewWindow,
}

impl GeminiProvider {
    pub fn new(window: WebviewWindow) -> Result<Self, AppError> {
        Ok(Self {
            api_key: Arc::new(RwLock::new(None)),
            window,
        })
    }

    pub async fn set_api_key(&self, api_key: String) -> Result<(), AppError> {
        let mut current_key = self.api_key.write().await;
        *current_key = Some(api_key);
        Ok(())
    }

    fn build_system_prompt(&self, session: &ExerciseSession) -> String {
        let mut prompt = String::new();
        prompt.push_str(
            "Eres CORTEX-IA, un asistente educativo especializado en una plataforma de ejercicios de programación. \
            Tu objetivo principal es guiar a los estudiantes en su proceso de aprendizaje. Formatea todas tus respuestas en Markdown.\n\n"
        );
        prompt.push_str("\
            1. NUNCA proporciones soluciones completas o código que resuelva directamente el ejercicio.\n\
            2. Enfócate en dar pistas constructivas y hacer preguntas que estimulen el pensamiento crítico.\n\
            3. Adapta tu nivel de ayuda según el progreso y comprensión del estudiante.\n\
            4. Si notas errores en el código, señálalos de manera constructiva y sugiere áreas de mejora.\n\
            5. Mantén un tono amigable y motivador, celebrando los avances del estudiante.\n\
            6. IMPORTANTE: Formatea TODAS tus respuestas usando Markdown para mejor legibilidad.\n\n"
        );

        let context = &session.context;
        prompt.push_str(&format!(
            "Contexto del ejercicio:\n\
            - **Título**: {}\n\
            - **Instrucciones**: {}\n",
            context.exercise_title, context.exercise_instructions
        ));

        if let Some(hints) = &context.exercise_hints {
            prompt.push_str(&format!("- **Pistas disponibles**: {}\n", hints));
        }

        prompt
    }

    async fn process_stream(
        &self,
        response: reqwest::Response,
    ) -> Result<(String, Option<UsageMetadata>), AppError> {
        let mut stream = response.bytes_stream();
        let mut full_response = String::new();
        let mut final_usage = None;
        let mut buffer = String::new();
        let mut total_candidates_tokens = 0u32;

        while let Some(chunk_result) = stream.next().await {
            match chunk_result {
                Ok(chunk) => {
                    if let Ok(text) = String::from_utf8(chunk.to_vec()) {
                        buffer.push_str(&text);

                        while let Some((json_str, rest)) = Self::extract_json_object(&buffer) {
                            let new_buffer = rest;

                            match serde_json::from_str::<StreamResponse>(json_str) {
                                Ok(response) => {
                                    if let Some(usage) = response.usage_metadata {
                                        total_candidates_tokens += usage.candidates_token_count;
                                        final_usage = Some(UsageMetadata {
                                            prompt_token_count: usage.prompt_token_count,
                                            candidates_token_count: total_candidates_tokens,
                                            total_token_count: usage.total_token_count
                                                + total_candidates_tokens,
                                        });
                                    }

                                    if let Some(candidate) = response.candidates.first() {
                                        if let Some(part) = candidate.content.parts.first() {
                                            debug!("Streaming chunk: {}", &part.text);
                                            self.window.emit("gemini-stream", &part.text).map_err(
                                                |e| {
                                                    error!("Failed to emit stream: {}", e);
                                                    AppError::TauriError(e)
                                                },
                                            )?;
                                            full_response.push_str(&part.text);
                                        }
                                    }
                                }
                                Err(e) => {
                                    if !e
                                        .to_string()
                                        .contains("missing field `candidatesTokenCount`")
                                    {
                                        error!(
                                            "Error parsing JSON object: {} | JSON: {}",
                                            e, json_str
                                        );
                                    }
                                }
                            }

                            buffer = new_buffer;
                        }
                    }
                }
                Err(e) => {
                    error!("Error reading stream chunk: {}", e);
                    return Err(AppError::RequestError(e));
                }
            }
        }

        self.window.emit("gemini-stream-end", ()).map_err(|e| {
            error!("Failed to emit stream end: {}", e);
            AppError::TauriError(e)
        })?;

        Ok((full_response, final_usage))
    }

    fn extract_json_object(buffer: &str) -> Option<(&str, String)> {
        let mut depth = 0;
        let mut start_index = None;
        let mut in_string = false;
        let mut escape_next = false;

        for (i, c) in buffer.char_indices() {
            if c == '{' && !in_string {
                start_index = Some(i);
                break;
            }
        }

        let start = start_index?;

        for (i, c) in buffer[start..].char_indices() {
            if escape_next {
                escape_next = false;
                continue;
            }

            match c {
                '"' if !escape_next => in_string = !in_string,
                '\\' if in_string => escape_next = true,
                '{' if !in_string => depth += 1,
                '}' if !in_string => {
                    depth -= 1;
                    if depth == 0 {
                        let end = start + i + 1;
                        let json_str = &buffer[start..end];
                        let rest = buffer[end..].trim_start().to_string();
                        return Some((json_str, rest));
                    }
                }
                _ => {}
            }
        }

        None
    }
}

#[async_trait]
impl AIProvider for GeminiProvider {
    fn provider_name(&self) -> &'static str {
        "gemini"
    }

    fn requires_api_key(&self) -> bool {
        true
    }

    async fn send_message(
        &self,
        session: &ExerciseSession,
        message: String,
    ) -> Result<AIResponse, AppError> {
        let api_key = self.api_key.read().await;
        let api_key = api_key
            .as_ref()
            .ok_or_else(|| AppError::AIServiceError("API key not configured for Gemini".into()))?;

        let system_prompt = self.build_system_prompt(session);

        let request = GeminiRequest {
            model: "gemini-1.5-flash-001".to_string(),
            contents: vec![
                Content {
                    role: "user".to_string(),
                    parts: vec![Part {
                        text: system_prompt,
                    }],
                },
                Content {
                    role: "user".to_string(),
                    parts: vec![Part { text: message }],
                },
            ],
        };

        let client = reqwest::Client::builder()
            .timeout(std::time::Duration::from_secs(30))
            .build()
            .map_err(AppError::RequestError)?;

        let url = format!("{}?key={}", GEMINI_API_URL, api_key);

        let response = client
            .post(&url)
            .header(header::CONTENT_TYPE, "application/json")
            .json(&request)
            .send()
            .await
            .map_err(|e| {
                error!("Failed to send request to Gemini: {}", e);
                AppError::RequestError(e)
            })?;

        if !response.status().is_success() {
            let error_text = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());

            return if let Ok(gemini_error) = serde_json::from_str::<GeminiError>(&error_text) {
                match (gemini_error.error.code, gemini_error.error.status.as_str()) {
                    (503, "UNAVAILABLE") => {
                        error!("Gemini model overloaded: {}", gemini_error.error.message);
                        Err(AppError::AIServiceError(
                            "El modelo está temporalmente sobrecargado. Por favor, intenta nuevamente en unos momentos o considera usar otro modelo.".into()
                        ))
                    }
                    (429, _) => {
                        error!("Gemini rate limit exceeded: {}", gemini_error.error.message);
                        Err(AppError::AIServiceError(
                            "Has alcanzado el límite de solicitudes. Por favor, espera un momento antes de intentar nuevamente.".into()
                        ))
                    }
                    _ => {
                        error!("Unexpected Gemini error: {:?}", gemini_error);
                        Err(AppError::AIServiceError(format!(
                            "Error del servicio de Gemini: {}",
                            gemini_error.error.message
                        )))
                    }
                }
            } else {
                error!("Non-Gemini API error: {}", error_text);
                Err(AppError::AIServiceError(
                    "Se produjo un error inesperado al comunicarse con Gemini. Por favor, intenta nuevamente.".into()
                ))
            };
        }

        let (response_text, usage_metadata) = self.process_stream(response).await?;

        Ok(AIResponse {
            text: response_text,
            usage: crate::session::Usage {
                input_tokens: usage_metadata.as_ref().map(|u| u.prompt_token_count).unwrap_or(0),
                output_tokens: usage_metadata
                    .as_ref()
                    .map(|u| u.candidates_token_count)
                    .unwrap_or(0),
            },
        })
    }

    fn as_any(&self) -> &dyn Any {
        self
    }
}
