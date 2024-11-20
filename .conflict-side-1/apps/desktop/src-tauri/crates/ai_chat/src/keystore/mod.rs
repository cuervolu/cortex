pub mod manager;

use keyring::Entry;
use error::AppError;
use serde::{Deserialize, Serialize};
use log::debug;
use std::collections::HashMap;

const SERVICE_NAME: &str = "com.bytech.cortex";

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct KeyStore {
    providers: HashMap<String, String>,
}

pub struct SecureKeystore {
    entry: Entry,
}

impl SecureKeystore {
    pub fn new(username: &str) -> Result<Self, AppError> {
        let entry = Entry::new(SERVICE_NAME, username)
            .map_err(|e| AppError::KeystoreError(format!("Failed to create keystore: {}", e)))?;

        Ok(Self { entry })
    }

    async fn get_store(&self) -> Result<KeyStore, AppError> {
        match self.entry.get_password() {
            Ok(json) => {
                serde_json::from_str(&json)
                    .map_err(|e| AppError::KeystoreError(format!("Failed to deserialize key data: {}", e)))
            }
            Err(keyring::Error::NoEntry) => Ok(KeyStore::default()),
            Err(e) => Err(AppError::KeystoreError(format!("Failed to retrieve store: {}", e)))
        }
    }

    async fn save_store(&self, store: &KeyStore) -> Result<(), AppError> {
        let json = serde_json::to_string(store)
            .map_err(|e| AppError::KeystoreError(format!("Failed to serialize key data: {}", e)))?;

        self.entry
            .set_password(&json)
            .map_err(|e| AppError::KeystoreError(format!("Failed to save store: {}", e)))
    }

    pub async fn save_api_key(&self, provider_name: &str, api_key: &str) -> Result<(), AppError> {
        if provider_name == "init" {
            debug!("Skipping save for initialization");
            return Ok(());
        }

        let mut store = self.get_store().await?;
        store.providers.insert(provider_name.to_string(), api_key.to_string());
        self.save_store(&store).await
    }

    pub async fn get_api_key(&self, provider_name: &str) -> Result<String, AppError> {
        if provider_name == "init" {
            return Err(AppError::KeystoreError("No API key configured".into()));
        }

        let store = self.get_store().await?;
        store.providers
            .get(provider_name)
            .cloned()
            .ok_or_else(|| AppError::KeystoreError(format!("No API key configured for {}", provider_name)))
    }

    pub async fn remove_api_key(&self, provider_name: &str) -> Result<(), AppError> {
        let mut store = self.get_store().await?;
        store.providers.remove(provider_name);
        self.save_store(&store).await
    }

    pub async fn remove_all_keys(&self) -> Result<(), AppError> {
        match self.entry.delete_credential() {
            Ok(_) => Ok(()),
            Err(keyring::Error::NoEntry) => Ok(()),
            Err(e) => Err(AppError::KeystoreError(format!("Failed to remove keys: {}", e)))
        }
    }
}