use crate::modules::{fetch_module_details, fetch_modules};
use crate::PaginatedModules;
use common::state::AppState;
use error::AppError;
use log::info;
use tauri::State;

#[tauri::command]
pub async fn fetch_all_modules(state: State<'_, AppState>) -> Result<PaginatedModules, AppError> {
    info!("Fetching all modules");
    match fetch_modules(state).await {
        Ok(modules) => Ok(modules),
        Err(e) => {
            log::error!("Failed to fetch modules: {:?}", e);
            Err(e)
        }
    }
}


#[tauri::command]
pub async fn get_module(slug: String, state: State<'_, AppState>) -> Result<PaginatedModules, AppError> {
    info!("Fetching module with slug: {}", slug);
    match fetch_module_details(&slug, state).await {
        Ok(module) => Ok(module),
        Err(e) => {
            log::error!("Failed to fetch module: {:?}", e);
            Err(e)
        }
    }
}