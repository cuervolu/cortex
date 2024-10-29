pub mod commands;

use crate::{Course, PaginatedCourses};
use common::state::AppState;
use common::{API_BASE_URL, CLIENT};
use error::AppError;
use tauri::State;

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