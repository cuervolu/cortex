use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ExerciseContext {
    pub exercise_id: String,
    pub exercise_slug: String,
    pub username: String,
    pub exercise_title: String,
    pub exercise_instructions: String,
    pub exercise_hints: Option<String>,
    pub editor_content: Option<String>,
    pub editor_language: Option<String>,
}