use crate::session::context::ExerciseContext;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ChatMessage {
    pub role: String,
    pub content: String,
    pub timestamp: chrono::DateTime<chrono::Utc>,
}

#[derive(Debug, Clone)]
pub struct ExerciseSession {
    pub context: ExerciseContext,
    pub messages: Vec<ChatMessage>,
}