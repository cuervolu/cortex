use log::info;
use crate::education::models::{RoadmapDetail, RoadmapListResponse};
use crate::education::roadmaps::{fetch_roadmaps, fetch_roadmaps_details};
use crate::error::AppError;

#[tauri::command]
pub async fn fetch_all_roadmaps() -> Result<RoadmapListResponse, AppError> {
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
pub async fn get_roadmap(slug: String) -> Result<RoadmapDetail, AppError> {
    info!("Fetching roadmap with slug: {}", slug);
    match fetch_roadmaps_details(&slug).await {
        Ok(roadmap) => Ok(roadmap),
        Err(e) => {
            log::error!("Failed to fetch roadmap: {:?}", e);
            Err(e)
        }
    }
}