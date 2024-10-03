use crate::education::models::ExerciseDetail;

use super::*;
use log::{error, info};

#[tauri::command]
pub async fn get_exercises() -> Result<Vec<ExerciseList>, AppError> {
    info!("Fetching exercises");
    match fetch_exercises().await {
        Ok(exercises) => Ok(exercises),
        Err(e) => {
            error!("Failed to fetch exercises: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_exercise_details(id: u32) -> Result<ExerciseDetail, AppError> {
    info!("Fetching exercise details for id: {}", id);
    match fetch_exercise_details(id).await {
        Ok(details) => Ok(details),
        Err(e) => {
            error!("Failed to fetch exercise details: {:?}", e);
            Err(e)
        }
    }
}
