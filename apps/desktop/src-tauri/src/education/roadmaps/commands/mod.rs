use crate::education::models::{PaginatedRoadmaps, RoadmapDetails};
use crate::education::roadmaps::{fetch_roadmaps, fetch_roadmaps_details};
use crate::error::AppError;
use log::info;
use tauri::State;
use crate::state::AppState;

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

// Haz cambios similares en education/exercises/commands.rs