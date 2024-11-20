use crate::lessons::{
    complete_lesson, create_lesson, delete_lesson, fetch_lesson_details, fetch_lessons,
    update_lesson,
};
use crate::{Lesson, LessonCreateRequest, LessonUpdateRequest, PaginatedLessons};
use common::state::AppState;
use error::AppError;
use log::{debug, error};
use tauri::State;

#[tauri::command]
pub async fn fetch_all_lessons(state: State<'_, AppState>) -> Result<PaginatedLessons, AppError> {
    debug!("Fetching all lessons");
    match fetch_lessons(state).await {
        Ok(lessons) => Ok(lessons),
        Err(e) => {
            error!("Failed to fetch lessons: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_lesson(slug: String, state: State<'_, AppState>) -> Result<Lesson, AppError> {
    debug!("Fetching lesson with slug: {}", slug);
    match fetch_lesson_details(&slug, state).await {
        Ok(lesson) => Ok(lesson),
        Err(e) => {
            error!("Failed to fetch lesson: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn create_new_lesson(
    request: LessonCreateRequest,
    state: State<'_, AppState>,
) -> Result<Lesson, AppError> {
    debug!("Creating new lesson with name: {}", request.name);
    match create_lesson(request, state).await {
        Ok(lesson) => {
            debug!("Successfully created lesson with id: {}", lesson.id);
            Ok(lesson)
        }
        Err(e) => {
            error!("Failed to create lesson: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn update_lesson_command(
    id: u64,
    request: LessonUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Lesson, AppError> {
    debug!("Updating lesson: {}", id);
    match update_lesson(id, request, state).await {
        Ok(lesson) => {
            debug!("Successfully updated lesson: {}", lesson.id);
            Ok(lesson)
        }
        Err(e) => {
            error!("Failed to update lesson: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn delete_lesson_command(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    debug!("Deleting lesson: {}", id);
    match delete_lesson(id, state).await {
        Ok(_) => {
            debug!("Successfully deleted lesson: {}", id);
            Ok(())
        }
        Err(e) => {
            error!("Failed to delete lesson: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn complete_lesson_command(
    lesson_id: u64,
    user_id: u64,
    state: State<'_, AppState>,
) -> Result<(), AppError> {
    debug!("Completing lesson: {} for user: {}", lesson_id, user_id);
    match complete_lesson(lesson_id, user_id, state).await {
        Ok(_) => {
            debug!("Successfully completed lesson: {}", lesson_id);
            Ok(())
        }
        Err(e) => {
            error!("Failed to complete lesson: {:?}", e);
            Err(e)
        }
    }
}
