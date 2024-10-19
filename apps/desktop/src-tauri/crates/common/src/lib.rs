use std::sync::LazyLock;
use reqwest::Client;

pub mod state;

pub const API_BASE_URL: &str = "http://localhost:8088/api/v1";

pub static CLIENT: LazyLock<Client> = LazyLock::new(|| {
    Client::builder()
        .build()
        .expect("Failed to build reqwest client")
});
