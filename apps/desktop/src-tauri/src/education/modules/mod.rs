pub(crate) mod commands;

use tauri::State;
use crate::{API_BASE_URL, CLIENT};
use crate::education::models::PaginatedModules;
use crate::error::AppError;
use crate::state::AppState;

pub async fn fetch_modules(state: State<'_, AppState>) -> Result<PaginatedModules, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/module", API_BASE_URL))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<PaginatedModules>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_module_details(slug: &str, state: State<'_, AppState>) -> Result<PaginatedModules, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/module/{}", API_BASE_URL, slug))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<PaginatedModules>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

