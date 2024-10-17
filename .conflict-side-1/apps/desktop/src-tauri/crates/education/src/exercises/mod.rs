pub mod commands;

use log::error;
use tauri::State;
use crate::{ExerciseDetails, PaginatedExercises};
use error::AppError;
use common::{API_BASE_URL, CLIENT};
use common::state::AppState;

pub async fn fetch_exercises(state: State<'_, AppState>) -> Result<PaginatedExercises, AppError> {
    let token = state.token.lock().map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/exercises", API_BASE_URL))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    // Imprimir el cuerpo de la respuesta
    let body = response.text().await.map_err(AppError::RequestError)?;

    // Intentar deserializar el cuerpo
    let exercises = serde_json::from_str::<PaginatedExercises>(&body)
        .map_err(|e| {
            error!("Deserialization error: {:?}", e);
            AppError::DeserializationError(e)
        })?;

    Ok(exercises)
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