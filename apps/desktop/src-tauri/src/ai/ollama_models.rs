/*
    Ollama doesn't have an API Endpoint to fetch all models, so we have to scrape the website to 
    get the list of models, this is a temporary solution until Ollama provides an API Endpoint to
    fetch all models.
*/
use tauri_plugin_store::{with_store, Store, StoreCollection};
use scraper::{Html, Selector};
use serde::{Deserialize, Serialize};
use std::collections::HashMap;
use std::path::PathBuf;
use std::sync::Arc;
use std::time::Instant;
use chrono::{DateTime, Utc};
use lazy_static::lazy_static;
use tokio::sync::RwLock;
use log::{info, warn, error};
use reqwest::Client;
use serde_json::json;
use tauri::{AppHandle, Manager, Wry};
use crate::error::AppError;

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct OllamaModel {
    pub name: String,
    pub tags: Vec<String>,
    pub description: String,
    pub last_scraped: DateTime<Utc>,
}

lazy_static! {
    pub static ref OLLAMA_MODELS: Arc<RwLock<HashMap<String, OllamaModel>>> =
        Arc::new(RwLock::new(HashMap::new()));
      static ref LAST_FETCH: RwLock<Instant> = RwLock::new(Instant::now());
}

pub const STORE_PATH: &str = ".ollama_models.dat";

fn parse_selector(selector: &str, error_message: &str) -> Result<Selector, AppError> {
    Selector::parse(selector).map_err(|e| {
        error!("Failed to parse selector '{}': {:?}", selector, e);
        AppError::AIServiceError(error_message.to_string())
    })
}

fn extract_text(element: scraper::ElementRef, selector: &Selector) -> String {
    element.select(selector)
        .next()
        .map(|el| el.text().collect::<String>().trim().to_string())
        .unwrap_or_default()
}

async fn fetch_body(url: &str, client: &Client) -> Result<String, AppError> {
    info!("Sending request to {}", url);
    let response = client.get(url).send().await
        .map_err(|e| {
            error!("Failed to send request to Ollama website: {:?}", e);
            AppError::AIServiceError(format!("Failed to send request to Ollama website: {}", e))
        })?;

    info!("Received response from Ollama website with status: {}", response.status());

    response.text().await.map_err(|e| {
        error!("Failed to get response text: {:?}", e);
        AppError::AIServiceError(format!("Failed to get response text: {}", e))
    })
}

async fn fetch_ollama_models() -> Result<Vec<OllamaModel>, AppError> {
    info!("Fetching Ollama models from website");
    let url = "https://ollama.com/library";
    let client = Client::new();

    let body = fetch_body(url, &client).await?;
    info!("Successfully retrieved response body, length: {} bytes", body.len());

    let document = Html::parse_document(&body);

    let model_selector = parse_selector("li.flex.items-baseline", "Failed to parse model selector")?;
    let name_selector = parse_selector("h2 span", "Failed to parse name selector")?;
    let description_selector = parse_selector("p.max-w-md", "Failed to parse description selector")?;
    let tag_selector = parse_selector("span.inline-flex.items-center.rounded-md", "Failed to parse tag selector")?;

    let current_time = Utc::now();

    let models: Vec<OllamaModel> = document.select(&model_selector)
        .map(|model| {
            let name = extract_text(model, &name_selector);
            let description = extract_text(model, &description_selector);
            let tags: Vec<String> = model.select(&tag_selector)
                .map(|t| t.text().collect::<String>().trim().to_string())
                .collect();

            info!("Parsed model: {}", name);
            OllamaModel { name, tags, description, last_scraped: current_time }
        })
        .collect();

    if models.is_empty() {
        warn!("No models were parsed from the Ollama website");
        return Err(AppError::AIServiceError("No models found on Ollama website".to_string()));
    }

    info!("Successfully fetched and parsed {} Ollama models", models.len());
    Ok(models)
}


pub async fn update_ollama_models(app_handle: &AppHandle<Wry>, force: bool) -> Result<(), AppError> {
    let stores = app_handle.state::<StoreCollection<Wry>>();
    let path = PathBuf::from(STORE_PATH);

    let should_update = with_store(app_handle.clone(), stores.clone(), path.clone(), |store: &mut Store<Wry>| {
        let models: Vec<OllamaModel> = store.get("ollama_models")
            .and_then(|v| serde_json::from_value(v.clone()).ok())
            .unwrap_or_default();

        Ok(models.is_empty() || force)
    }).map_err(AppError::TauriStoreError)?;

    if !should_update {
        info!("Skipping Ollama models update, update not forced");
        return Ok(());
    }

    info!("Updating Ollama models cache");
    match fetch_ollama_models().await {
        Ok(models) => {
            with_store(app_handle.clone(), stores, path, |store: &mut Store<Wry>| {
                store.insert("ollama_models".to_string(), json!(models))?;
                store.save()?;
                Ok(())
            }).map_err(AppError::TauriStoreError)?;

            info!("Ollama models cache updated successfully with {} models", models.len());
            Ok(())
        }
        Err(e) => {
            error!("Failed to update Ollama models cache: {:?}", e);
            Err(e)
        }
    }
}

pub async fn get_ollama_models(app_handle: &AppHandle<Wry>) -> Result<Vec<OllamaModel>, AppError> {
    let stores = app_handle.state::<StoreCollection<Wry>>();
    let path = PathBuf::from(STORE_PATH);

    let mut models: Vec<OllamaModel> = Vec::new();
    let mut attempts = 0;
    const MAX_ATTEMPTS: u8 = 2;

    while attempts < MAX_ATTEMPTS {
        models = with_store(app_handle.clone(), stores.clone(), path.clone(), |store: &mut Store<Wry>| {
            Ok(store.get("ollama_models")
                .and_then(|value| serde_json::from_value(value.clone()).ok())
                .unwrap_or_default())
        }).map_err(AppError::TauriStoreError)?;

        if !models.is_empty() {
            break;
        }

        if attempts == 0 {
            update_ollama_models(app_handle, true).await?;
        }

        attempts += 1;
    }

    if models.is_empty() {
        Err(AppError::AIServiceError("Failed to retrieve Ollama models after multiple attempts".to_string()))
    } else {
        Ok(models)
    }
}

pub async fn init(app_handle: &AppHandle<Wry>) -> Result<(), AppError> {
    info!("Initializing Ollama models cache");
    get_ollama_models(app_handle).await.map(|_| ())
}

pub async fn force_update_ollama_models(app_handle: &AppHandle<Wry>) -> Result<(), AppError> {
    info!("Forcing update of Ollama models");
    update_ollama_models(app_handle, true).await
}