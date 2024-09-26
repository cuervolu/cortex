use thiserror::Error;

#[derive(Error, Debug)]
pub enum AppError {
    #[error("Tauri error: {0}")]
    TauriError(#[from] tauri::Error),

    #[error("IO error: {0}")]
    Io(#[from] std::io::Error),

    #[error("Failed to execute system command")]
    CommandExecutionError,

    #[error("Ollama not found")]
    OllamaNotFound,

    #[error("Unsupported operating system")]
    UnsupportedOS,

    #[error("Failed to initialize Ollama")]
    OllamaInitializationError,

    #[error("AI Service error: {0}")]
    AIServiceError(String),
}

impl serde::Serialize for AppError {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::ser::Serializer,
    {
        serializer.serialize_str(self.to_string().as_ref())
    }
}