use crate::{Course, PaginatedRoadmaps, RoadmapDetails};
use crate::roadmaps::{fetch_roadmaps, fetch_roadmaps_details, fetch_course_from_roadmap};
use error::AppError;
use log::info;
use tauri::State;
use common::state::AppState;

#[tauri::command]
pub async fn fetch_all_roadmaps(state: State<'_, AppState>) -> Result<PaginatedRoadmaps, AppError> {
    info!("Fetching all roadmaps");
    match fetch_roadmaps(state).await {
        Ok(roadmaps) => Ok(roadmaps),
        Err(e) => {
            log::error!("Failed to fetch roadmaps: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_roadmap(slug: String, state: State<'_, AppState>) -> Result<RoadmapDetails, AppError> {
    info!("Fetching roadmap with slug: {}", slug);
    match fetch_roadmaps_details(&slug, state).await {
        Ok(roadmap) => Ok(roadmap),
        Err(e) => {
            log::error!("Failed to fetch roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn fetch_roadmap_course(roadmap_slug: String, course_slug: String, state: State<'_, AppState>) -> Result<Course, AppError> {
    info!("Fetching course with slug: {} from roadmap with slug: {}", course_slug, roadmap_slug);
    match fetch_course_from_roadmap(&roadmap_slug, &course_slug, state).await {
        Ok(course) => Ok(course),
        Err(e) => {
            log::error!("Failed to fetch course: {:?}", e);
            Err(e)
        }
    }
}
