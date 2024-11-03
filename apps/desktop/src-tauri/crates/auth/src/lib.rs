pub mod commands;

use base64::{engine::general_purpose::URL_SAFE_NO_PAD, Engine};
use rand::{distributions::Alphanumeric, thread_rng, Rng};
use serde::{Deserialize, Serialize};
use sha2::{Digest, Sha256};

#[derive(Debug, Serialize, Deserialize)]
pub struct AuthResponse {
    token: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct OAuthError {
    message: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct OAuthConfig {
    pub provider: String,
    pub code_verifier: String,
    pub code_challenge: String,
    pub port: u16,
}

fn generate_random_string(length: usize) -> String {
    thread_rng()
        .sample_iter(&Alphanumeric)
        .take(length)
        .map(char::from)
        .collect()
}

fn generate_code_challenge(code_verifier: &str) -> String {
    let mut hasher = Sha256::new();
    hasher.update(code_verifier.as_bytes());
    let result = hasher.finalize();
    URL_SAFE_NO_PAD.encode(result)
}
