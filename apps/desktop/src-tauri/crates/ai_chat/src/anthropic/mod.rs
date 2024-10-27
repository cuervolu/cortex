mod service;
pub mod commands;
mod context;

use serde::{Deserialize, Serialize};

#[derive(Debug, Deserialize)]
#[serde(untagged)]
pub enum AnthropicResponse {
    Success(MessageResponse),
    Error(ErrorResponse),
}

#[derive(Debug, Deserialize)]
pub struct MessageResponse {
    pub content: Vec<ContentBlock>,
    pub id: String,
    pub model: String,
    pub role: String,
    pub stop_reason: Option<StopReason>,
    pub stop_sequence: Option<String>,
    #[serde(rename = "type")]
    pub response_type: String,
    pub usage: Usage,
}

#[derive(Debug, Deserialize)]
#[serde(tag = "type")]
pub enum ContentBlock {
    #[serde(rename = "text")]
    Text { text: String },
}

#[derive(Debug, Deserialize)]
#[serde(rename_all = "snake_case")]
pub enum StopReason {
    EndTurn,
    MaxTokens,
    StopSequence,
    ToolUse,
}

#[derive(Debug, Deserialize, Serialize)]
pub struct Usage {
    pub input_tokens: u32,
    pub output_tokens: u32,
}

#[derive(Debug, Deserialize)]
pub struct ErrorResponse {
    #[serde(rename = "type")]
    pub error_type: String,
    pub error: Error,
}

#[derive(Debug, Deserialize)]
pub struct Error {
    #[serde(rename = "type")]
    pub error_type: String,
    pub message: String,
}

// Implementaciones útiles
impl MessageResponse {
    /// Obtiene el texto de la respuesta concatenando todos los bloques de texto
    pub fn get_text(&self) -> String {
        self.content
            .iter()
            .map(|block| match block {
                ContentBlock::Text { text } => text.as_str(),
            })
            .collect::<Vec<&str>>()
            .join("")
    }
}

impl std::fmt::Display for ErrorResponse {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "Error tipo '{}': {}",
            self.error.error_type, self.error.message
        )
    }
}