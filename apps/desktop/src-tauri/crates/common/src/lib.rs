use error::AppError;
use reqwest::multipart::Part;
use reqwest::Client;
use std::sync::LazyLock;
use tauri::AppHandle;
use tauri_plugin_dialog::DialogExt;
use tauri_plugin_fs::FsExt;

pub mod state;

pub const API_BASE_URL: &str = "http://localhost:8088/api/v1";
pub const MAX_IMAGE_SIZE: usize = 5 * 1024 * 1024;

pub static CLIENT: LazyLock<Client> = LazyLock::new(|| {
    Client::builder()
        .build()
        .expect("Failed to build reqwest client")
});


pub fn validate_image_type(data: &[u8]) -> Result<(), AppError> {
    let kind = infer::get(data)
        .ok_or_else(|| AppError::ValidationError("Unable to determine file type".to_string()))?;

    match kind.mime_type() {
        "image/jpeg" | "image/png" | "image/webp" | "image/jpg" => Ok(()),
        _ => Err(AppError::ValidationError(
            "Invalid file type. Only JPEG,JPG, PNG and WebP images are allowed".to_string()
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
    
    let part = Part::bytes(image_data)
        .file_name(filename.clone())
        .mime_str(kind.mime_type())?;

    Ok((part, filename))
}

pub fn handle_image_selection(
    app: &AppHandle,
    title: Option<&str>
) -> Result<Vec<u8>, AppError> {
    let mut dialog = app.dialog().file();
    
    if let Some(title_text) = title {
        dialog = dialog.set_title(title_text);
    }
    
    let file_path = dialog
        .add_filter("Images", &["png", "jpg", "jpeg", "webp"])
        .blocking_pick_file()
        .ok_or_else(|| AppError::UserCancelled("File selection cancelled".into()))?;
    
    app.fs()
        .read(file_path)
        .map_err(|e| AppError::FileError(format!("Failed to read image file: {}", e)))
}