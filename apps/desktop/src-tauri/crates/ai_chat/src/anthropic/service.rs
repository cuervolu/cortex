use error::AppError;
use reqwest::{Client, header};
use serde::Serialize;
use std::time::Duration;
use crate::anthropic::{AnthropicResponse, context::ChatContext};

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
}

pub struct AnthropicService {
    client: Client,
    api_key: String,
}

impl AnthropicService {
    pub fn new(api_key: String) -> Result<Self, AppError> {
        let mut headers = header::HeaderMap::new();
        headers.insert(
            "anthropic-version",
            header::HeaderValue::from_static(ANTHROPIC_VERSION),
        );
        headers.insert(
            header::CONTENT_TYPE,
            header::HeaderValue::from_static("application/json"),
        );

        let client = Client::builder()
            .timeout(Duration::from_secs(300))
            .default_headers(headers)
            .build()
            .map_err(AppError::RequestError)?;

        Ok(Self { client, api_key })
    }

    pub async fn chat_with_context(
        &self,
        context: &ChatContext,
        user_message: &str
    ) -> Result<AnthropicResponse, AppError> {
        let system_prompt = context.build_system_prompt();
        let full_message = format!("{}\n\nMensaje del estudiante: {}", system_prompt, user_message);

        let message = Message {
            role: "user".to_string(),
            content: full_message,
        };

        let request = ChatRequest {
            model: "claude-3-sonnet-20240229".to_string(),
            max_tokens: 1024,
            messages: vec![message],
        };

        let response = self
            .client
            .post(ANTHROPIC_API_URL)
            .header("x-api-key", &self.api_key)
            .json(&request)
            .send()
            .await
            .map_err(AppError::RequestError)?;

        let response_text = response.text().await
            .map_err(AppError::RequestError)?;

        serde_json::from_str(&response_text)
            .map_err(AppError::DeserializationError)
    }

    pub async fn chat_with_context_and_history(
        &self,
        context: &ChatContext,
        messages: &[(String, String)],
        user_message: &str
    ) -> Result<AnthropicResponse, AppError> {
        let system_prompt = context.build_system_prompt();

        let mut chat_messages = vec![
            Message {
                role: "system".to_string(),
                content: system_prompt,
            }
        ];
        
        chat_messages.extend(messages.iter().map(|(role, content)| Message {
            role: role.clone(),
            content: content.clone()
        }));
        
        chat_messages.push(Message {
            role: "user".to_string(),
            content: user_message.to_string(),
        });

        let request = ChatRequest {
            model: "claude-3-sonnet-20240229".to_string(),
            max_tokens: 1024,
            messages: chat_messages,
        };

        let response = self
            .client
            .post(ANTHROPIC_API_URL)
            .header("x-api-key", &self.api_key)
            .json(&request)
            .send()
            .await
            .map_err(AppError::RequestError)?;

        let response_text = response.text().await
            .map_err(AppError::RequestError)?;

        serde_json::from_str(&response_text)
            .map_err(AppError::DeserializationError)
    }
}