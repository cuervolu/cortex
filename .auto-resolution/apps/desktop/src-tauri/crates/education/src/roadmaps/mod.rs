pub mod commands;

use tauri::State;
use crate::{Course, PaginatedRoadmaps, RoadmapDetails};
use error::AppError;
use common::{API_BASE_URL, CLIENT};
use common::state::AppState;

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

pub async fn fetch_course_from_roadmap(
    roadmap_slug: &str,
    course_slug: &str,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!(
            "{}/api/v1/education/roadmap/{}/course/{}",
            API_BASE_URL,
            roadmap_slug,
            course_slug
        ))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Course>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}