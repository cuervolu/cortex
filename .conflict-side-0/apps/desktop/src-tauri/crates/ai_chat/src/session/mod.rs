use serde::{Deserialize, Serialize};

pub(crate) mod session;
pub(crate) mod context;
pub mod manager;

#[derive(Debug, Serialize, Deserialize, Default)]
pub struct Usage {
    pub(crate) input_tokens: u32,
    pub(crate) output_tokens: u32,
}

#[derive(Debug, Serialize, Deserialize, Default)]
pub struct AIResponse {
    pub(crate) text: String,
    pub(crate) usage: Usage,
}