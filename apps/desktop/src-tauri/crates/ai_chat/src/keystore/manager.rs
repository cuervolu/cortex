use std::sync::Arc;
use tokio::sync::RwLock;
use error::AppError;
use crate::keystore::SecureKeystore;

pub struct KeystoreManager {
    keystore: Arc<RwLock<Option<SecureKeystore>>>,
    initialized: Arc<RwLock<bool>>,
}

impl KeystoreManager {
    pub fn new() -> Self {
        Self {
            keystore: Arc::new(RwLock::new(None)),
            initialized: Arc::new(RwLock::new(false)),
        }
    }

    pub async fn initialize(&self, user_id: i32) -> Result<(), AppError> {
        let mut initialized = self.initialized.write().await;
        if *initialized {
            return Ok(());
        }

        let keystore = SecureKeystore::new(&user_id.to_string())?;
        let mut store = self.keystore.write().await;
        *store = Some(keystore);
        *initialized = true;
        Ok(())
    }

    pub async fn is_initialized(&self) -> bool {
        *self.initialized.read().await
    }

    pub async fn save_api_key(&self, provider_name: &str, api_key: &str) -> Result<(), AppError> {
        let store = self.keystore.read().await;
        match store.as_ref() {
            Some(keystore) => keystore.save_api_key(provider_name, api_key).await,
            None => Err(AppError::KeystoreError("Keystore not initialized. Please log in first.".into()))
        }
    }

    pub async fn get_api_key(&self, provider_name: &str) -> Result<String, AppError> {
        let store = self.keystore.read().await;
        match store.as_ref() {
            Some(keystore) => keystore.get_api_key(provider_name).await,
            None => Err(AppError::KeystoreError("Keystore not initialized. Please log in first.".into()))
        }
    }

    pub async fn remove_provider_key(&self, provider_name: &str) -> Result<(), AppError> {
        let store = self.keystore.read().await;
        match store.as_ref() {
            Some(keystore) => keystore.remove_api_key(provider_name).await,
            None => Err(AppError::KeystoreError("Keystore not initialized. Please log in first.".into()))
        }
    }

    pub async fn remove_all_keys(&self) -> Result<(), AppError> {
        let store = self.keystore.read().await;
        match store.as_ref() {
            Some(keystore) => keystore.remove_all_keys().await,
            None => Err(AppError::KeystoreError("Keystore not initialized. Please log in first.".into()))
        }
    }

    pub async fn clear(&self) -> Result<(), AppError> {
        let mut store = self.keystore.write().await;
        *store = None;
        let mut initialized = self.initialized.write().await;
        *initialized = false;
        Ok(())
    }
}

impl Default for KeystoreManager {
    fn default() -> Self {
        Self::new()
    }
}