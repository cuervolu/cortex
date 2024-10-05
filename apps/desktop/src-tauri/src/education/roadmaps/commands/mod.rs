use crate::education::models::{PaginatedRoadmaps, RoadmapDetails};
use crate::education::roadmaps::{fetch_roadmaps, fetch_roadmaps_details};
use crate::error::AppError;
use log::info;

#[tauri::command]
pub async fn fetch_all_roadmaps() -> Result<PaginatedRoadmaps, AppError> {
    info!("Fetching all roadmaps");
    match fetch_roadmaps().await {
        Ok(roadmaps) => Ok(roadmaps),
        Err(e) => {
            log::error!("Failed to fetch roadmaps: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_roadmap(slug: String) -> Result<RoadmapDetails, AppError> {
    info!("Fetching roadmap with slug: {}", slug);
    match fetch_roadmaps_details(&slug).await {
        Ok(roadmap) => Ok(roadmap),
        Err(e) => {
            log::error!("Failed to fetch roadmap: {:?}", e);
            Err(e)
        }
    }
}
