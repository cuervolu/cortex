use crate::lessons::{fetch_lesson_details, fetch_lessons};
use crate::PaginatedLessons;
use common::state::AppState;
use error::AppError;
use log::info;
use tauri::State;

#[tauri::command]
pub async fn fetch_all_lessons(state: State<'_, AppState>) -> Result<PaginatedLessons, AppError> {
    info!("Fetching all lessons");
    match fetch_lessons(state).await {
        Ok(lessons) => Ok(lessons),
        Err(e) => {
            log::error!("Failed to fetch lessons: {:?}", e);
            Err(e)
        }
    }
}


#[tauri::command]
pub async fn get_lesson(slug: String, state: State<'_, AppState>) -> Result<PaginatedLessons, AppError> {
    info!("Fetching lesson with slug: {}", slug);
    match fetch_lesson_details(&slug, state).await {
        Ok(lesson) => Ok(lesson),
        Err(e) => {
            log::error!("Failed to fetch lesson: {:?}", e);
            Err(e)
        }
    }
}