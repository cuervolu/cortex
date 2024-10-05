pub mod commands;

use crate::education::models::{ExerciseDetails, PaginatedExercises};
use crate::error::AppError;
use crate::{API_BASE_URL, CLIENT};

// TODO: Replace this with a real token
const AUTH_TOKEN: &str = "eyJhbGciOiJIUzM4NCJ9.eyJmdWxsbmFtZSI6IsOBbmdlbCBDdWVydm8iLCJzdWIiOiJjdWVydm9sdSIsImlhdCI6MTcyODEwMDYwOSwiZXhwIjoxNzI4MTg3MDA5LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.oSlebEYCww4G1qOdedElXW9qMWR2Eo2LNlvNq7EYFlr1EZvlekcr5_ShLf-HJWNM";

pub async fn fetch_exercises() -> Result<Vec<PaginatedExercises>, AppError> {
    let response = CLIENT
        .get(format!("{}/exercises", API_BASE_URL))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Vec<PaginatedExercises>>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_exercise_details(id: u32) -> Result<ExerciseDetails, AppError> {
    let response = CLIENT
        .get(format!("{}/exercises/{}/details", API_BASE_URL, id))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<ExerciseDetails>()
        .await
        .map_err(AppError::RequestError)?;
    Ok(response)
}
