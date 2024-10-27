use log::info;
use serde::Serialize;
use tauri::ipc::private::tracing::debug;
use error::AppError;
use crate::anthropic::{AnthropicResponse, Usage};
use crate::anthropic::service::AnthropicService;
use crate::anthropic::context::ChatContext;

#[derive(Debug, Serialize)]
pub struct ChatResponse {
    text: String,
    usage: Usage,
}

impl ChatResponse {
    fn from_anthropic_response(response: AnthropicResponse) -> Result<Self, AppError> {
        match response {
            AnthropicResponse::Success(message) => Ok(ChatResponse {
                text: message.get_text(),
                usage: message.usage,
            }),
            AnthropicResponse::Error(error) => Err(AppError::AIServiceError(error.to_string())),
        }
    }
}

#[tauri::command]
pub async fn chat_with_claude(
    username: String,
    exercise_title: String,
    exercise_instructions: String,
    exercise_hints: Option<String>,
    message: String,
    editor_content: Option<String>,
    editor_language: Option<String>,
    api_key: String
) -> Result<ChatResponse, AppError> {
    let service = AnthropicService::new(api_key)?;

    let context = ChatContext::new(
        username,
        exercise_title,
        exercise_instructions,
        exercise_hints,
        editor_content,
        editor_language,
    );
    debug!("Chatting with Claude: {:?}", context);
    let response = service.chat_with_context(&context, &message).await?;
    ChatResponse::from_anthropic_response(response)
}

#[tauri::command]
pub async fn chat_with_history(
    username: String,
    exercise_title: String,
    exercise_instructions: String,
    exercise_hints: Option<String>,
    messages: Vec<(String, String)>,
    editor_content: Option<String>,
    editor_language: Option<String>,
    api_key: String
) -> Result<ChatResponse, AppError> {
    let service = AnthropicService::new(api_key)?;

    let context = ChatContext::new(
        username,
        exercise_title,
        exercise_instructions,
        exercise_hints,
        editor_content,
        editor_language,
    );

    let last_message = messages.last()
        .map(|(_, content)| content.as_str())
        .unwrap_or("");
    debug!("Chatting with history: {:?}", context);
    let response = service.chat_with_context_and_history(&context, &messages, last_message).await?;
    ChatResponse::from_anthropic_response(response)
}