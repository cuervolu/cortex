pub mod commands;

use crate::{Module, ModuleCreateRequest, ModuleUpdateRequest, PaginatedModules};
use common::state::AppState;
use common::{create_image_part, validate_image_type, API_BASE_URL, CLIENT, MAX_IMAGE_SIZE};
use error::AppError;
use reqwest::multipart::Form;
use tauri::State;

pub async fn fetch_modules(state: State<'_, AppState>) -> Result<PaginatedModules, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
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

pub async fn fetch_module_details(
    slug: &str,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/education/module/slug/{}", API_BASE_URL, slug))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Module>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn create_module(
    request: ModuleCreateRequest,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .post(format!("{}/education/module", API_BASE_URL))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to create module: {}", error_msg)));
    }

    response.json::<Module>().await.map_err(AppError::RequestError)
}

pub async fn update_module(
    id: u64,
    request: ModuleUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .patch(format!("{}/education/module/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to update module: {}", error_msg)));
    }

    response.json::<Module>().await.map_err(AppError::RequestError)
}

pub async fn delete_module(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .delete(format!("{}/education/module/{}", API_BASE_URL, id))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to delete module: {}", error_msg)));
    }

    Ok(())
}

pub async fn upload_module_image(
    module_id: u64,
    image_data: Vec<u8>,
    alt_text: Option<String>,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
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
        .post(format!("{}/education/module/{}/image", API_BASE_URL, module_id))
        .bearer_auth(token)
        .multipart(form)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!("Failed to upload module image: {}", error_msg)));
    }

    response.json::<Module>().await.map_err(AppError::RequestError)
}
