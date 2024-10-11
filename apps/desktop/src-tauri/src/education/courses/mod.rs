pub(crate) mod commands;

use tauri::State;
use crate::education::models::{PaginatedCourses, Course};
use crate::error::AppError;
use crate::{API_BASE_URL, CLIENT};
use crate::state::AppState;

pub async fn fetch_courses(state: State<'_,AppState>) -> Result<PaginatedCourses, AppError>{
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
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

pub async fn fetch_course_details(slug: &str, state: State<'_,AppState>) -> Result<Course, AppError>{
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/course/{}", API_BASE_URL, slug))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Course>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}