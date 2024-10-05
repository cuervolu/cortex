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

    #[error("Failed to execute system command")]
    CommandExecutionError(String),

    #[error("Window not found")]
    WindowNotFound,

    #[error("Ollama not found")]
    OllamaNotFound,

    #[error("Unsupported operating system")]
    UnsupportedOS,

    #[error("Failed to initialize Ollama")]
    OllamaInitializationError,

    #[error("AI Service error: {0}")]
    AIServiceError(String),

    #[error("Failed to lock chat context")]
    ContextLockError,

    #[error("Context not found")]
    ContextNotFound,

    #[error("Unknown error")]
    Unknown(#[from] anyhow::Error),
}

impl serde::Serialize for AppError {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::ser::Serializer,
    {
        serializer.serialize_str(self.to_string().as_ref())
    }
}
