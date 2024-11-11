pub mod commands;

use crate::{
    Course, PaginatedRoadmaps, Roadmap, RoadmapCreateRequest, RoadmapDetails, RoadmapUpdateRequest,
};
use common::state::AppState;
use common::{API_BASE_URL, CLIENT};
use error::AppError;
use tauri::State;

pub async fn fetch_roadmaps(state: State<'_, AppState>) -> Result<PaginatedRoadmaps, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
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

pub async fn fetch_roadmaps_details(
    slug: &str,
    state: State<'_, AppState>,
) -> Result<RoadmapDetails, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
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
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!(
            "{}/api/v1/education/roadmap/{}/course/{}",
            API_BASE_URL, roadmap_slug, course_slug
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

pub async fn create_roadmap(
    request: RoadmapCreateRequest,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .post(format!("{}/education/roadmap", API_BASE_URL))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to create roadmap: {}", error_msg)));
    }

    response.json::<Roadmap>().await.map_err(AppError::RequestError)
}

pub async fn update_roadmap(
    id: u64,
    request: RoadmapUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .patch(format!("{}/education/roadmap/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to update roadmap: {}", error_msg)));
    }

    response.json::<Roadmap>().await.map_err(AppError::RequestError)
}

pub async fn delete_roadmap(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .delete(format!("{}/education/roadmap/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to delete roadmap: {}", error_msg)));
    }

    Ok(())
}
