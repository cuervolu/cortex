use super::*;
use log::{error, info};
use tauri::State;

#[tauri::command]
pub async fn get_exercises(state: State<'_, AppState>) -> Result<Vec<PaginatedExercises>, AppError> {
    info!("Fetching exercises");
    fetch_exercises(state).await
}

#[tauri::command]
pub async fn get_exercise_details(id: u32, state: State<'_, AppState>) -> Result<ExerciseDetails, AppError> {
    info!("Fetching exercise details for id: {}", id);
    fetch_exercise_details(id, state).await
}