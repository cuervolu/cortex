use crate::roadmaps::{
    create_roadmap, delete_roadmap, fetch_course_from_roadmap, fetch_roadmaps,
    fetch_roadmaps_details, update_roadmap,
};
use crate::{
    Course, PaginatedRoadmaps, Roadmap, RoadmapCreateRequest, RoadmapDetails, RoadmapUpdateRequest,
};
use common::state::AppState;
use error::AppError;
use log::{debug, error};
use tauri::{AppHandle, State};
use common::handle_content_image_upload;

#[tauri::command]
pub async fn fetch_all_roadmaps(state: State<'_, AppState>) -> Result<PaginatedRoadmaps, AppError> {
    debug!("Fetching all roadmaps");
    match fetch_roadmaps(state).await {
        Ok(roadmaps) => Ok(roadmaps),
        Err(e) => {
            log::error!("Failed to fetch roadmaps: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_roadmap(
    slug: String,
    state: State<'_, AppState>,
) -> Result<RoadmapDetails, AppError> {
    debug!("Fetching roadmap with slug: {}", slug);
    match fetch_roadmaps_details(&slug, state).await {
        Ok(roadmap) => Ok(roadmap),
        Err(e) => {
            log::error!("Failed to fetch roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn fetch_roadmap_course(
    roadmap_slug: String,
    course_slug: String,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    debug!("Fetching course with slug: {} from roadmap with slug: {}", course_slug, roadmap_slug);
    match fetch_course_from_roadmap(&roadmap_slug, &course_slug, state).await {
        Ok(course) => Ok(course),
        Err(e) => {
            log::error!("Failed to fetch course: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn create_new_roadmap(
    request: RoadmapCreateRequest,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    debug!("Creating new roadmap with title: {}", request.title);
    match create_roadmap(request, state).await {
        Ok(roadmap) => {
            debug!("Successfully created roadmap with id: {}", roadmap.id);
            Ok(roadmap)
        }
        Err(e) => {
            log::error!("Failed to create roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn upload_roadmap_image_command(
    roadmap_id: u64,
    image_path: String,
    alt_text: Option<String>,
    app: AppHandle,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    handle_content_image_upload::<Roadmap>(
        roadmap_id,
        image_path,
        alt_text,
        "roadmap",
        &app,
        state,
    ).await
}

#[tauri::command]
pub async fn update_roadmap_command(
    id: u64,
    request: RoadmapUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    debug!("Updating roadmap: {}", id);
    match update_roadmap(id, request, state).await {
        Ok(roadmap) => {
            debug!("Successfully updated roadmap: {}", roadmap.id);
            Ok(roadmap)
        }
        Err(e) => {
            log::error!("Failed to update roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn delete_roadmap_command(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    debug!("Deleting roadmap: {}", id);
    match delete_roadmap(id, state).await {
        Ok(_) => {
            debug!("Successfully deleted roadmap: {}", id);
            Ok(())
        }
        Err(e) => {
            error!("Failed to delete roadmap: {:?}", e);
            Err(e)
        }
    }
}
