use crate::state::AppState;
use error::AppError;
use log::debug;
use reqwest::multipart::{Form, Part};
use reqwest::Client;
use serde::de::DeserializeOwned;
use std::path::PathBuf;
use std::sync::LazyLock;
use tauri::{AppHandle, State};
use tauri_plugin_fs::FsExt;

pub mod state;

pub const API_BASE_URL: &str = "http://localhost:8088/api/v1";
pub const MAX_IMAGE_SIZE: usize = 5 * 1024 * 1024;

pub static CLIENT: LazyLock<Client> =
    LazyLock::new(|| Client::builder().build().expect("Failed to build reqwest client"));

pub fn validate_image_type(data: &[u8]) -> Result<(), AppError> {
    let kind = infer::get(data)
        .ok_or_else(|| AppError::ValidationError("Unable to determine file type".to_string()))?;

    match kind.mime_type() {
        "image/jpeg" | "image/png" | "image/webp" | "image/jpg" => Ok(()),
        _ => Err(AppError::ValidationError(
            "Invalid file type. Only JPEG,JPG, PNG and WebP images are allowed".to_string(),
        )),
    }
}

pub fn create_image_part(image_data: Vec<u8>) -> Result<(Part, String), AppError> {
    let kind = infer::get(&image_data)
        .ok_or_else(|| AppError::ValidationError("Unable to determine file type".to_string()))?;

    let extension = match kind.mime_type() {
        "image/jpeg" => "jpg",
        "image/jpg" => "jpg",
        "image/png" => "png",
        "image/webp" => "webp",
        _ => return Err(AppError::ValidationError("Unsupported image type".to_string())),
    };

    let filename = format!("image.{}", extension);

    let part = Part::bytes(image_data).file_name(filename.clone()).mime_str(kind.mime_type())?;

    Ok((part, filename))
}

pub async fn handle_content_image_upload<T>(
    content_id: u64,
    image_path: String,
    alt_text: Option<String>,
    content_type: &str,
    app: &AppHandle,
    state: State<'_, AppState>,
) -> Result<T, AppError>
where
    T: DeserializeOwned,
{
    debug!("Processing image upload for {} {}: {}", content_type, content_id, image_path);

    let path = PathBuf::from(image_path);
    let image_data = app
        .fs()
        .read(path)
        .map_err(|e| AppError::FileError(format!("Failed to read image file: {}", e)))?;
    
    if image_data.len() > MAX_IMAGE_SIZE {
        return Err(AppError::ValidationError(
            "Image size exceeds maximum allowed size of 5MB".to_string(),
        ));
    }

    validate_image_type(&image_data)?;

    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let mut form = Form::new();
    let (image_part, _) = create_image_part(image_data)?;
    form = form.part("image", image_part);

    if let Some(alt_text) = alt_text {
        form = form.text("altText", alt_text);
    }

    // Hacer la petición
    let response = CLIENT
        .post(format!("{}/education/{}/{}/image", API_BASE_URL, content_type, content_id))
        .bearer_auth(token)
        .multipart(form)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if !response.status().is_success() {
        let error_msg = response.text().await.unwrap_or_else(|_| "Unknown error".to_string());
        return Err(AppError::ApiError(format!(
            "Failed to upload {} image: {}",
            content_type, error_msg
        )));
    }

    response.json::<T>().await.map_err(AppError::RequestError)
}
