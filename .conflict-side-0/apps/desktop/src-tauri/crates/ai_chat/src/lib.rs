pub mod ollama;
pub mod ollama_models;
pub mod commands;

use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct OllamaModelInfo {
    name: String,
    id: String,
    size: String,
    modified: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct OllamaModelDetails {
    architecture: String,
    parameters: String,
    context_length: String,
    embedding_length: String,
    quantization: String,
    license: String,
}
