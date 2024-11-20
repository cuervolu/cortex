use thiserror::Error;

#[derive(Error, Debug)]
pub enum AppError {
    #[error("Tauri error: {0}")]
    TauriError(#[from] tauri::Error),

    #[error("Tauri store error: {0}")]
    TauriStoreError(#[from] tauri_plugin_store::Error),

    #[error("Request failed: {0}")]
    RequestError(#[from] reqwest::Error),

    #[error("IO error: {0}")]
    Io(#[from] std::io::Error),

    #[error("Invalid header value: {0}")]
    InvalidHeader(#[from] reqwest::header::InvalidHeaderValue),

    #[error("Failed to execute system command")]
    CommandExecutionError(String),

    #[error("Window not found")]
    WindowNotFound,

    #[error("Failed to initialize Ollama")]
    OllamaInitializationError,

    #[error("AI Service error: {0}")]
    AIServiceError(String),

    #[error("Failed to lock chat context")]
    ContextLockError,

    #[error("Context not found")]
    ContextNotFound,

    #[error("No authentication token found")]
    NoTokenError,

    #[error("Failed to deserialize response: {0}")]
    DeserializationError(#[from] serde_json::Error),

    #[error("Unknown error")]
    Unknown(#[from] anyhow::Error),

    #[error("Session not found")]
    SessionError(String),

    #[error("Provider error: {0}")]
    ProviderError(String),

    #[error("Keystore error: {0}")]
    KeystoreError(String),

    #[error("Code execution timeout: {0}")]
    CodeExecutionTimeout(String),

    #[error("OAuth error: {0}")]
    OAuthError(String),

    #[error("API error: {0}")]
    ApiError(String),
    
    #[error("Validation error: {0}")]
    ValidationError(String),

    #[error("User cancelled operation: {0}")]
    UserCancelled(String),
    
    #[error("File error: {0}")]
    FileError(String),

    #[error("Unauthorized access")]
    UnauthorizedError,
}

impl serde::Serialize for AppError {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::ser::Serializer,
    {
        serializer.serialize_str(self.to_string().as_ref())
    }
}