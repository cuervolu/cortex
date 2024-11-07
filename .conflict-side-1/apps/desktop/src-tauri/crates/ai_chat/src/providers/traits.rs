use crate::session::session::ExerciseSession;
use crate::session::AIResponse;
use async_trait::async_trait;
use error::AppError;
use std::any::Any;

#[async_trait]
pub trait AIProvider: Send + Sync {
    fn provider_name(&self) -> &'static str;
    fn requires_api_key(&self) -> bool;

    async fn send_message(
        &self,
        session: &ExerciseSession,
        message: String,
    ) -> Result<AIResponse, AppError>;

    fn as_any(&self) -> &dyn Any;
}