pub mod manager;

use keyring::Entry;
use error::AppError;
use serde::{Deserialize, Serialize};
use log::{debug, error};

const SERVICE_NAME: &str = "com.bytech.cortex";

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct KeyStore {
    provider_name: String,
    api_key: String,
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

    pub async fn save_api_key(&self, provider_name: &str, api_key: &str) -> Result<(), AppError> {
        // Si el provider es 'init', no hacemos nada y retornamos OK
        if provider_name == "init" {
            debug!("Skipping save for initialization");
            return Ok(());
        }

        let store = KeyStore {
            provider_name: provider_name.to_string(),
            api_key: api_key.to_string(),
        };

        let json = serde_json::to_string(&store)
            .map_err(|e| AppError::KeystoreError(format!("Failed to serialize key data: {}", e)))?;

        self.entry
            .set_password(&json)
            .map_err(|e| AppError::KeystoreError(format!("Failed to save key: {}", e)))?;

        Ok(())
    }

    pub async fn get_api_key(&self, provider_name: &str) -> Result<String, AppError> {
        // Si el provider es 'init', retornamos un error específico
        if provider_name == "init" {
            return Err(AppError::KeystoreError("No API key configured".into()));
        }

        match self.entry.get_password() {
            Ok(json) => {
                let store: KeyStore = serde_json::from_str(&json)
                    .map_err(|e| AppError::KeystoreError(format!("Failed to deserialize key data: {}", e)))?;

                if store.provider_name != provider_name {
                    return Err(AppError::KeystoreError("No API key configured".into()));
                }

                Ok(store.api_key)
            }
            Err(keyring::Error::NoEntry) => {
                Err(AppError::KeystoreError("No API key configured".into()))
            }
            Err(e) => {
                error!("Failed to retrieve key from keystore: {}", e);
                Err(AppError::KeystoreError(format!("Failed to retrieve key: {}", e)))
            }
        }
    }

    pub async fn remove_api_key(&self) -> Result<(), AppError> {
        match self.entry.delete_credential() {
            Ok(_) => Ok(()),
            Err(keyring::Error::NoEntry) => Ok(()),
            Err(e) => Err(AppError::KeystoreError(format!("Failed to remove key: {}", e)))
        }
    }
}