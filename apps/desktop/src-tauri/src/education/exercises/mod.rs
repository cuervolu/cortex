pub mod commands;

use crate::education::models::ExerciseList;
use crate::error::AppError;
use crate::{API_BASE_URL, CLIENT};

use super::models::ExerciseDetail;
// TODO: Replace this with a real token
const AUTH_TOKEN: &str = "eyJhbGciOiJIUzM4NCJ9.eyJmdWxsbmFtZSI6IsOBbmdlbCBDdWVydm8iLCJzdWIiOiJjdWVydm9sdSIsImlhdCI6MTcyNzk5MTA2OCwiZXhwIjoxNzI4MDc3NDY4LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl19.Mm2MQ0IALF2exGy5c_hArXKRmNnNG0gCv_0MATwen2CecFr_w2u3au8hVRgfu8FF";

pub async fn fetch_exercises() -> Result<Vec<ExerciseList>, AppError> {
    let response = CLIENT
        .get(format!("{}/exercises", API_BASE_URL))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<Vec<ExerciseList>>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn fetch_exercise_details(id: u32) -> Result<ExerciseDetail, AppError> {
    let response = CLIENT
        .get(format!("{}/exercises/{}/details", API_BASE_URL, id))
        .bearer_auth(AUTH_TOKEN)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<ExerciseDetail>()
        .await
        .map_err(AppError::RequestError)?;
    Ok(response)
}
