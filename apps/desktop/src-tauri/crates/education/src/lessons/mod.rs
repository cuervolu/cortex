pub mod commands;

use crate::{Lesson, LessonCreateRequest, LessonUpdateRequest, PaginatedLessons};
use common::state::AppState;
use common::{API_BASE_URL, CLIENT};
use error::AppError;
use tauri::State;

pub async fn fetch_lessons(state: State<'_, AppState>) -> Result<PaginatedLessons, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/lesson", API_BASE_URL))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<PaginatedLessons>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_lesson_details(
    slug: &str,
    state: State<'_, AppState>,
) -> Result<Lesson, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/lesson/slug/{}", API_BASE_URL, slug))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Lesson>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn create_lesson(
    request: LessonCreateRequest,
    state: State<'_, AppState>,
) -> Result<Lesson, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .post(format!("{}/education/lesson", API_BASE_URL))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to create lesson: {}", error_msg)));
    }

    response.json::<Lesson>().await.map_err(AppError::RequestError)
}

pub async fn update_lesson(
    id: u64,
    request: LessonUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Lesson, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .patch(format!("{}/education/lesson/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to update lesson: {}", error_msg)));
    }

    response.json::<Lesson>().await.map_err(AppError::RequestError)
}

pub async fn delete_lesson(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .delete(format!("{}/education/lesson/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to delete lesson: {}", error_msg)));
    }

    Ok(())
}

pub async fn complete_lesson(
    lesson_id: u64,
    user_id: u64,
    state: State<'_, AppState>,
) -> Result<(), AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .post(format!("{}/education/lesson/{}/complete", API_BASE_URL, lesson_id))
        .bearer_auth(token)
        .json(&user_id)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to complete lesson: {}", error_msg)));
    }

    Ok(())
}
