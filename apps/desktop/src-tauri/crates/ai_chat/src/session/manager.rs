use crate::keystore::manager::KeystoreManager;
use crate::providers::claude::ClaudeProvider;
use crate::providers::ollama::OllamaProvider;
use crate::providers::traits::AIProvider;
use crate::session::context::ExerciseContext;
use crate::session::session::{ChatMessage, ExerciseSession};
use error::AppError;
use log::debug;
use std::collections::HashMap;
use std::sync::Arc;
use tauri::WebviewWindow;
use tokio::sync::RwLock;
use crate::providers::gemini::GeminiProvider;

pub struct AISessionManager {
    sessions: Arc<RwLock<HashMap<String, ExerciseSession>>>,
    current_provider: Arc<RwLock<Box<dyn AIProvider>>>,
    keystore_manager: Arc<KeystoreManager>,
}

impl AISessionManager {
    pub fn new(window: WebviewWindow) -> Result<Self, AppError> {
        let keystore_manager = Arc::new(KeystoreManager::new());
        let default_provider: Box<dyn AIProvider> = Box::new(OllamaProvider::new(window));

        Ok(Self {
            sessions: Arc::new(RwLock::new(HashMap::new())),
            current_provider: Arc::new(RwLock::new(default_provider)),
            keystore_manager,
        })
    }

    pub async fn initialize_keystore(&self, user_id: i32) -> Result<(), AppError> {
        self.keystore_manager.initialize(user_id).await
    }


    pub async fn start_session(&self, context: ExerciseContext) -> Result<(), AppError> {
        let session = ExerciseSession {
            context,
            messages: Vec::new(),
        };
        let mut sessions = self.sessions.write().await;
        sessions.insert(session.context.exercise_id.clone(), session);
        Ok(())
    }

    pub async fn send_message(&self, exercise_id: &str, message: &str) -> Result<(), AppError> {
        let mut sessions = self.sessions.write().await;
        let session = sessions
            .get_mut(exercise_id)
            .ok_or_else(|| AppError::SessionError("Session not found".into()))?;

        let provider = self.current_provider.read().await;
        let response = provider.send_message(session, message.to_string()).await?;

        session.messages.push(ChatMessage {
            role: "user".to_string(),
            content: message.to_string(),
            timestamp: chrono::Utc::now(),
        });

        session.messages.push(ChatMessage {
            role: "assistant".to_string(),
            content: response.text,
            timestamp: chrono::Utc::now(),
        });

        Ok(())
    }

    pub async fn end_session(&self, exercise_id: &str) -> Result<(), AppError> {
        let mut sessions = self.sessions.write().await;
        sessions.remove(exercise_id);
        Ok(())
    }

    pub async fn set_provider(
        &self,
        provider_name: &str,
        window: WebviewWindow,
    ) -> Result<(), AppError> {
        let new_provider: Box<dyn AIProvider> = match provider_name {
            "ollama" => Box::new(OllamaProvider::new(window)),
            "claude" => Box::new(ClaudeProvider::new(window)?),
            "gemini" => Box::new(GeminiProvider::new(window)?),
            _ => return Err(AppError::ProviderError("Unknown provider".into())),
        };

        if new_provider.requires_api_key() {
            if let Ok(api_key) = self.keystore_manager.get_api_key(provider_name).await {
                Self::configure_provider_api_key(&new_provider, provider_name, api_key).await?;
            }
        }

        let mut provider = self.current_provider.write().await;
        *provider = new_provider;

        Ok(())
    }

    pub async fn get_provider_api_key(&self, provider_name: &str) -> Result<String, AppError> {
        self.keystore_manager.get_api_key(provider_name).await
    }

    pub async fn set_provider_api_key(
        &self,
        provider_name: &str,
        api_key: Option<String>,
        user_id: i32,
    ) -> Result<(), AppError> {
        debug!("Setting API key for provider {}", provider_name);
        
        self.keystore_manager.initialize(user_id).await?;

        // If a new API key is provided, save it.
        if let Some(key) = api_key {
            self.keystore_manager.save_api_key(provider_name, &key).await?;

             // If the current provider matches the one we are configuring,
            // we also update its API key
            let provider = self.current_provider.read().await;
            if provider.provider_name() == provider_name {
                Self::configure_provider_api_key(&provider, provider_name, key).await?;
            }
        }

        Ok(())
    }

    pub async fn remove_provider_api_key(&self) -> Result<(), AppError> {
        self.keystore_manager.remove_provider_key(self.current_provider.read().await.provider_name()).await
    }

    pub async fn remove_all_api_keys(&self) -> Result<(), AppError> {
        self.keystore_manager.remove_all_keys().await
    }

    async fn configure_provider_api_key(
        provider: &Box<dyn AIProvider>,
        provider_name: &str,
        api_key: String,
    ) -> Result<(), AppError> {
        match provider_name {
            "claude" => {
                if let Some(provider) = provider.as_any().downcast_ref::<ClaudeProvider>() {
                    provider.set_api_key(api_key).await?;
                }
            }
            "gemini" => {
                if let Some(provider) = provider.as_any().downcast_ref::<GeminiProvider>() {
                    provider.set_api_key(api_key).await?;
                }
            }
            _ => return Err(AppError::ProviderError(format!(
                "Provider {} does not support API key configuration",
                provider_name
            ))),
        }
        Ok(())
    }

}
