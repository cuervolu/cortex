use crate::providers::traits::AIProvider;
use crate::session::session::ExerciseSession;
use crate::session::AIResponse;
use async_trait::async_trait;
use error::AppError;
use log::{debug, error};
use reqwest::header::{self};
use serde::{Deserialize, Serialize};
use std::any::Any;
use std::sync::Arc;
use tauri::{Emitter, WebviewWindow};
use tokio::sync::RwLock;

const GEMINI_API_URL: &str =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

#[derive(Debug, Serialize)]
struct SystemInstruction {
    parts: TextPart,
}

#[derive(Debug, Serialize, Deserialize)]
struct Contents {
    parts: TextPart,
}

#[derive(Debug, Serialize, Deserialize)]
struct TextPart {
    text: String,
}

#[derive(Debug, Serialize)]
struct GeminiRequest {
    system_instruction: SystemInstruction,
    contents: Contents,
}

#[derive(Debug, Deserialize)]
struct GeminiResponse {
    candidates: Vec<Candidate>,
    #[serde(rename = "usageMetadata")]
    usage_metadata: UsageMetadata,
}

#[derive(Debug, Deserialize)]
struct Candidate {
    content: Content,
    #[serde(rename = "finishReason")]
    finish_reason: String,
}

#[derive(Debug, Deserialize)]
struct Content {
    parts: Vec<TextPart>,
}

#[derive(Debug, Deserialize)]
struct UsageMetadata {
    #[serde(rename = "promptTokenCount")]
    prompt_token_count: u32,
    #[serde(rename = "candidatesTokenCount")]
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

        prompt.push_str(&format!("\n**Estudiante**: {}\n", context.username));

        if let Some(content) = &context.editor_content {
            prompt.push_str("\n**Código actual del estudiante**");
            if let Some(lang) = &context.editor_language {
                prompt.push_str(&format!(" en {}:\n", lang));
            } else {
                prompt.push_str(":\n");
            }
            prompt.push_str(&format!(
                "```{}\n{}\n```\n",
                context.editor_language.as_deref().unwrap_or(""),
                content
            ));
        }

        prompt.push_str(
            "\n**Recordatorios**:\n\
            - Guía sin resolver directamente\n\
            - Fomenta el pensamiento independiente\n\
            - Celebra los logros\n\
            - Mantén un tono motivador\n\
            - Usa Markdown para formatear tus respuestas\n",
        );
        prompt
    }

    async fn send_request(
        &self,
        request: GeminiRequest,
        api_key: &str,
    ) -> Result<GeminiResponse, AppError> {
        let client = reqwest::Client::new();
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
            error!("Gemini API error: {}", error_text);
            return Err(AppError::AIServiceError(error_text));
        }

        response.json::<GeminiResponse>().await.map_err(|e| {
            error!("Failed to parse Gemini response: {}", e);
            AppError::RequestError(e)
        })
    }

    async fn process_response(&self, response: GeminiResponse) -> Result<String, AppError> {
        if let Some(candidate) = response.candidates.first() {
            if let Some(part) = candidate.content.parts.first() {
                self.window.emit("gemini-stream", &part.text).map_err(|e| {
                    error!("Failed to emit stream: {}", e);
                    AppError::TauriError(e)
                })?;

                self.window.emit("gemini-stream-end", ()).map_err(|e| {
                    error!("Failed to emit stream end: {}", e);
                    AppError::TauriError(e)
                })?;

                debug!(
                    "Candidates token count: {:?}",
                    response.usage_metadata.candidates_token_count
                );
                debug!(
                    "Prompt Token count: {}, Total: {}",
                    response.usage_metadata.prompt_token_count,
                    response.usage_metadata.total_token_count
                );

                return Ok(part.text.clone());
            }
        }

        Err(AppError::AIServiceError("No response content".into()))
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
            system_instruction: SystemInstruction {
                parts: TextPart {
                    text: system_prompt,
                },
            },
            contents: Contents {
                parts: TextPart { text: message },
            },
        };

        let response = self.send_request(request, api_key).await?;
        let response_text = self.process_response(response).await?;

        Ok(AIResponse {
            text: response_text,
            usage: Default::default(),
        })
    }

    fn as_any(&self) -> &dyn Any {
        self
    }
}
