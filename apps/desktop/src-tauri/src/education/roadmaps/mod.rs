pub(crate) mod commands;

use tauri::State;
use crate::education::models::{PaginatedRoadmaps, RoadmapDetails};
use crate::error::AppError;
use crate::{API_BASE_URL, CLIENT};
use crate::state::AppState;

pub async fn fetch_roadmaps(state: State<'_, AppState>) -> Result<PaginatedRoadmaps, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/roadmap", API_BASE_URL))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<PaginatedRoadmaps>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_roadmaps_details(slug: &str, state: State<'_, AppState>) -> Result<RoadmapDetails, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/roadmap/{}", API_BASE_URL, slug))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<RoadmapDetails>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}