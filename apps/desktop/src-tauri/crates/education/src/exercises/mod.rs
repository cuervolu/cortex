pub mod commands;

use crate::{
    CodeExecutionRequest, CodeExecutionResult, CodeExecutionSubmissionResponse, ExerciseDetails,
    PaginatedExercises,
};
use common::state::AppState;
use common::{API_BASE_URL, CLIENT};
use error::AppError;
use log::{debug, error};
use std::time::Duration;
use tauri::State;

pub async fn fetch_exercises(state: State<'_, AppState>) -> Result<PaginatedExercises, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/exercises", API_BASE_URL))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?;
    
    let body = response.text().await.map_err(AppError::RequestError)?;
    
    let exercises = serde_json::from_str::<PaginatedExercises>(&body).map_err(|e| {
        error!("Deserialization error: {:?}", e);
        AppError::DeserializationError(e)
    })?;

    Ok(exercises)
}

pub async fn fetch_exercise_details(
    id: u32,
    state: State<'_, AppState>,
) -> Result<ExerciseDetails, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
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

// Exercise Submission
pub async fn submit_code_execution(
    code: String,
    language: String,
    exercise_id: u64,
    state: State<'_, AppState>,
) -> Result<CodeExecutionSubmissionResponse, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let request = CodeExecutionRequest {
        code,
        language,
        exercise_id,
    };

    let response = CLIENT
        .post(format!("{}/engine/execute", API_BASE_URL))
        .bearer_auth(token)
        .json(&request)
        .send()
        .await
        .map_err(AppError::RequestError)?
        .json::<CodeExecutionSubmissionResponse>()
        .await
        .map_err(AppError::RequestError)?;

    Ok(response)
}

pub async fn get_execution_result(
    task_id: &str,
    state: State<'_, AppState>,
) -> Result<Option<CodeExecutionResult>, AppError> {
    let token = state
        .token
        .lock()
        .map_err(|_| AppError::ContextLockError)?
        .clone()
        .ok_or(AppError::NoTokenError)?;

    let response = CLIENT
        .get(format!("{}/engine/result/{}", API_BASE_URL, task_id))
        .bearer_auth(token)
        .send()
        .await
        .map_err(AppError::RequestError)?;

    if response.status() == reqwest::StatusCode::NOT_FOUND {
        return Ok(None);
    }
    
    let response_text = response.text().await.map_err(AppError::RequestError)?;
    
    match serde_json::from_str::<CodeExecutionResult>(&response_text) {
        Ok(result) => Ok(Some(result)),
        Err(e) => {
            error!("Failed to deserialize response: {}", e);
            error!("Response body: {}", response_text);
            Err(AppError::DeserializationError(e))
        }
    }
}

pub async fn poll_execution_result(
    task_id: &str,
    state: State<'_, AppState>,
    max_attempts: u32,
    delay_ms: u64,
) -> Result<CodeExecutionResult, AppError> {
    for attempt in 0..max_attempts {
        debug!("Polling attempt {} of {} for task {}", attempt + 1, max_attempts, task_id);

        match get_execution_result(task_id, state.clone()).await {
            Ok(Some(result)) => {
                debug!("Got result on attempt {}", attempt + 1);
                return Ok(result);
            }
            Ok(None) => {
                debug!("No result yet on attempt {}", attempt + 1);
            }
            Err(e) => {
                debug!("Error on attempt {}: {:?}", attempt + 1, e);
                if matches!(e, AppError::DeserializationError(_)) {
                    error!("Deserialization error on attempt {}: {:?}", attempt + 1, e);
                } else {
                    return Err(e);
                }
            }
        }

        if attempt < max_attempts - 1 {
            tokio::time::sleep(Duration::from_millis(delay_ms)).await;
        }
    }

    Err(AppError::CodeExecutionTimeout(format!(
        "Maximum attempts ({}) reached waiting for execution result for task ID: {}",
        max_attempts, task_id
    )))
}