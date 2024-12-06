pub mod commands;

use crate::{Course, CourseCreateRequest, CourseUpdateRequest, PaginatedCourses, Module, PaginatedResponse};
use common::state::AppState;
use common::{SortQueryParams ,API_BASE_URL, CLIENT};
use error::AppError;
use log::{debug, error};
use tauri::State;

pub async fn fetch_courses(state: State<'_, AppState>) -> Result<PaginatedCourses, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/course", API_BASE_URL))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<PaginatedCourses>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_course_by_id(id: u64, state: State<'_, AppState>) -> Result<Course, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/course/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Course>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_course_details(
    slug: &str,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/course/slug/{}", API_BASE_URL, slug))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Course>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn create_course(
    request: CourseCreateRequest,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .post(format!("{}/education/course", API_BASE_URL))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to create course: {}", error_msg)));
    }

    response.json::<Course>().await.map_err(AppError::RequestError)
}

pub async fn update_course(
    id: u64,
    request: CourseUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .patch(format!("{}/education/course/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to update course: {}", error_msg)));
    }

    response.json::<Course>().await.map_err(AppError::RequestError)
}

pub async fn delete_course(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .delete(format!("{}/education/course/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to delete course: {}", error_msg)));
    }

    Ok(())
}

pub async fn get_course_modules(
    slug: &str,
    state: State<'_, AppState>,
    page: Option<u32>,
    size: Option<u32>,
    sort: Option<Vec<String>>,
) -> Result<PaginatedResponse<Module>, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let params = SortQueryParams { page, size, sort };
    let url = format!(
        "{}/education/course/{}/modules{}",
        API_BASE_URL,
        slug,
        params.to_query_string()
    );

    debug!("Fetching course modules from: {}", url);

    let response = CLIENT.get(&url).bearer_auth(token).send().await.map_err(|e| {
        error!("Failed to fetch course modules: {:?}", e);
        AppError::RequestError(e)
    })?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to get course modules: {}", error_msg)));
    }

    let paginated_response = response.json::<PaginatedResponse<Module>>().await.map_err(|e| {
        error!("Failed to deserialize course modules: {:?}", e);
        AppError::RequestError(e)
    })?;

    debug!("Successfully fetched {} course modules", paginated_response.content.len());
    Ok(paginated_response)
}

