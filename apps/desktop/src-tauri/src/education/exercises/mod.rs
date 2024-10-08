pub mod commands;

use tauri::State;
use crate::education::models::{ExerciseDetails, PaginatedExercises};
use crate::error::AppError;
use crate::{API_BASE_URL, CLIENT};
use crate::state::AppState;

pub async fn fetch_exercises(state: State<'_, AppState>) -> Result<Vec<PaginatedExercises>, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/exercises", API_BASE_URL))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Vec<PaginatedExercises>>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_exercise_details(id: u32, state: State<'_, AppState>) -> Result<ExerciseDetails, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/exercises/{}/details", API_BASE_URL, id))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<ExerciseDetails>()
        .await
        .map_err(AppError::RequestError)?;
    Ok(response)
}