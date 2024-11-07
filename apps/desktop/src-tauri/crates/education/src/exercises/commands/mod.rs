use super::*;
use log::info;
use tauri::State;

#[tauri::command]
pub async fn get_exercises(state: State<'_, AppState>) -> Result<PaginatedExercises, AppError> {
    info!("Fetching exercises");
    fetch_exercises(state).await
}

#[tauri::command]
pub async fn get_exercise_details(id: u32, state: State<'_, AppState>) -> Result<ExerciseDetails, AppError> {
    info!("Fetching exercise details for id: {}", id);
    fetch_exercise_details(id, state).await
}

#[tauri::command]
pub async fn execute_code(
    code: String,
    language: String,
    exercise_id: u64,
    state: State<'_, AppState>,
) -> Result<CodeExecutionSubmissionResponse, AppError> {
    info!("Executing code for exercise: {}", exercise_id);
    submit_code_execution(code, language, exercise_id, state).await
}

#[tauri::command]
pub async fn get_code_execution_result(
    task_id: String,
    state: State<'_, AppState>,
) -> Result<CodeExecutionResult, AppError> {
    info!("Getting execution result for task: {}", task_id);
    // We try to poll the execution result 10 times with a 1-second delay between each poll
    poll_execution_result(&task_id, state, 10, 1000).await
}