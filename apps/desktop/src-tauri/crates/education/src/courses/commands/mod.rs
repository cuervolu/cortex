use super::*;
use log::info;
use tauri::State;

#[tauri::command]
pub async fn get_courses(state: State<'_, AppState>) -> Result<PaginatedCourses, AppError> {
    info!("Fetching courses");
    fetch_courses(state).await
}

#[tauri::command]
pub async fn get_course_details(slug: String, state: State<'_, AppState>) -> Result<Course, AppError> {
    info!("Fetching course details for slug: {}", slug);
    fetch_course_details(&slug, state).await
}