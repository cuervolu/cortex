pub mod commands;

use crate::{
    Course, PaginatedResponse, PaginatedRoadmaps, Roadmap, RoadmapCourseAssignment,
    RoadmapCreateRequest, RoadmapDetails, RoadmapUpdateRequest,
};
use common::state::AppState;
use common::{SortQueryParams, API_BASE_URL, CLIENT};
use error::AppError;
use log::{debug, error};
use tauri::State;

pub async fn fetch_roadmaps(
    state: State<'_, AppState>,
    page: Option<u32>,
    size: Option<u32>,
    sort: Option<Vec<String>>,
    is_admin: bool,
) -> Result<PaginatedRoadmaps, AppError> {
    if is_admin {
        let user = state
            .user
            .lock()
            .map_err(|_| AppError::ContextLockError)?
            .clone()
            .ok_or(AppError::NoTokenError)?;

        if !user.roles.iter().any(|role| role == "ADMIN" || role == "MODERATOR") {
            return Err(AppError::UnauthorizedError);
        }
    }

    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let params = SortQueryParams { page, size, sort };
    let endpoint = if is_admin {
        format!("{}/education/roadmap/admin", API_BASE_URL)
    } else {
        format!("{}/education/roadmap", API_BASE_URL)
    };

    let url = format!("{}{}", endpoint, params.to_query_string());

    let response = CLIENT
        .get(&url)
        .bearer_auth(token)
        .send()
        .await
        .map_err(|e| {
            log::error!("Failed to fetch roadmaps: {:?}", e);
            AppError::RequestError(e)
        })?
        .json::<PaginatedRoadmaps>()
        .await
        .map_err(|e| {
            log::error!("Failed to parse roadmaps response: {:?}", e);
            AppError::RequestError(e)
        })?;

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

pub async fn get_roadmap_courses(
    roadmap_id: u64,
    state: State<'_, AppState>,
    page: Option<u32>,
    size: Option<u32>,
    sort: Option<Vec<String>>,
) -> Result<PaginatedResponse<Course>, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let params = SortQueryParams { page, size, sort };
    let url = format!(
        "{}/education/roadmap/{}/courses{}",
        API_BASE_URL,
        roadmap_id,
        params.to_query_string()
    );

    debug!("Fetching roadmap courses from: {}", url);

    let response = CLIENT
        .get(&url)
        .bearer_auth(token)
        .send()
        .await
        .map_err(|e| {
            error!("Failed to fetch roadmap courses: {:?}", e);
            AppError::RequestError(e)
        })?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to get roadmap courses: {}", error_msg)));
    }

    let paginated_response = response
        .json::<PaginatedResponse<Course>>()
        .await
        .map_err(|e| {
            error!("Failed to deserialize roadmap courses: {:?}", e);
            AppError::RequestError(e)
        })?;

    debug!("Successfully fetched {} roadmap courses", paginated_response.content.len());
    Ok(paginated_response)
}

pub async fn get_available_courses(
    roadmap_id: u64,
    state: State<'_, AppState>,
    page: Option<u32>,
    size: Option<u32>,
    sort: Option<Vec<String>>,
    include_unpublished: Option<bool>,
) -> Result<PaginatedResponse<Course>, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let params = SortQueryParams { page, size, sort };
    let query_string = params.to_query_string();

    let url = if let Some(include_unpub) = include_unpublished {
        let separator = if query_string.is_empty() { "?" } else { "&" };
        format!(
            "{}/education/roadmap/{}/available-courses{}{}includeUnpublished={}",
            API_BASE_URL, roadmap_id, query_string, separator, include_unpub
        )
    } else {
        format!(
            "{}/education/roadmap/{}/available-courses{}",
            API_BASE_URL, roadmap_id, query_string
        )
    };

    debug!("Fetching available courses from: {}", url);

    let response = CLIENT
        .get(&url)
        .bearer_auth(token)
        .send()
        .await
        .map_err(|e| {
            error!("Failed to fetch available courses: {:?}", e);
            AppError::RequestError(e)
        })?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to get available courses: {}", error_msg)));
    }

    let paginated_response = response
        .json::<PaginatedResponse<Course>>()
        .await
        .map_err(|e| {
            error!("Failed to deserialize available courses: {:?}", e);
            AppError::RequestError(e)
        })?;

    debug!("Successfully fetched {} available courses", paginated_response.content.len());
    Ok(paginated_response)
}

pub async fn update_roadmap_courses(
    roadmap_id: u64,
    assignments: RoadmapCourseAssignment,
    state: State<'_, AppState>,
) -> Result<(), AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .put(format!("{}/education/roadmap/{}/courses", API_BASE_URL, roadmap_id))
        .bearer_auth(token)
        .json(&assignments)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to update roadmap courses: {}", error_msg)));
    }

    Ok(())
}
