use crate::modules::{
    create_module, delete_module, fetch_module_details, fetch_modules, update_module,
    upload_module_image,
};
use crate::{Module, ModuleCreateRequest, ModuleUpdateRequest, PaginatedModules};
use common::handle_image_selection;
use common::state::AppState;
use error::AppError;
use log::{debug, error};
use tauri::State;
#[tauri::command]
pub async fn fetch_all_modules(state: State<'_, AppState>) -> Result<PaginatedModules, AppError> {
    debug!("Fetching all modules");
    match fetch_modules(state).await {
        Ok(modules) => Ok(modules),
        Err(e) => {
            log::error!("Failed to fetch modules: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_module(
    slug: String,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
    debug!("Fetching module with slug: {}", slug);
    match fetch_module_details(&slug, state).await {
        Ok(module) => Ok(module),
        Err(e) => {
            log::error!("Failed to fetch module: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn create_new_module(
    request: ModuleCreateRequest,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
    debug!("Creating new module with name: {}", request.name);
    match create_module(request, state).await {
        Ok(module) => {
            debug!("Successfully created module with id: {}", module.id);
            Ok(module)
        }
        Err(e) => {
            error!("Failed to create module: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn update_module_command(
    id: u64,
    request: ModuleUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
    debug!("Updating module: {}", id);
    match update_module(id, request, state).await {
        Ok(module) => {
            debug!("Successfully updated module: {}", module.id);
            Ok(module)
        }
        Err(e) => {
            error!("Failed to update module: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn delete_module_command(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    debug!("Deleting module: {}", id);
    match delete_module(id, state).await {
        Ok(_) => {
            debug!("Successfully deleted module: {}", id);
            Ok(())
        }
        Err(e) => {
            error!("Failed to delete module: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn upload_module_image_command(
    module_id: u64,
    alt_text: Option<String>,
    app: tauri::AppHandle,
    state: State<'_, AppState>,
) -> Result<Module, AppError> {
    debug!("Opening file dialog for module image upload: {}", module_id);

    let image_data = handle_image_selection(&app, Some("Select module image"))?;

    match upload_module_image(module_id, image_data, alt_text, state).await {
        Ok(module) => {
            debug!("Successfully uploaded image for module: {}", module_id);
            Ok(module)
        }
        Err(e) => {
            error!("Failed to upload module image: {:?}", e);
            Err(e)
        }
    }
}
