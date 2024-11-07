pub mod commands;

use crate::{Course, CourseCreateRequest, CourseUpdateRequest, PaginatedCourses};
use common::state::AppState;
use common::{create_image_part, validate_image_type, API_BASE_URL, CLIENT, MAX_IMAGE_SIZE};
use error::AppError;
use reqwest::multipart::Form;
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

pub async fn upload_course_image(
    course_id: u64,
    image_data: Vec<u8>,
    alt_text: Option<String>,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let mut form = Form::new();

    if image_data.len() > MAX_IMAGE_SIZE {
        return Err(AppError::ValidationError(
            "Image size exceeds maximum allowed size of 5MB".to_string(),
        ));
    }

    validate_image_type(&image_data)?;

    let (image_part, _) = create_image_part(image_data)?;
    form = form.part("image", image_part);

    if let Some(alt_text) = alt_text {
        form = form.text("altText", alt_text);
    }

    let response = CLIENT
        .post(format!("{}/education/course/{}/image", API_BASE_URL, course_id))
        .bearer_auth(token)
        .multipart(form)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to upload course image: {}", error_msg)));
    }

    response.json::<Course>().await.map_err(AppError::RequestError)
}
