use super::*;
use common::handle_image_selection;
use log::{debug, error};
use tauri::State;

#[tauri::command]
pub async fn fetch_all_courses(state: State<'_, AppState>) -> Result<PaginatedCourses, AppError> {
    debug!("Fetching all courses");
    match fetch_courses(state).await {
        Ok(courses) => Ok(courses),
        Err(e) => {
            error!("Failed to fetch courses: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_course(slug: String, state: State<'_, AppState>) -> Result<Course, AppError> {
    debug!("Fetching course with slug: {}", slug);
    match fetch_course_details(&slug, state).await {
        Ok(course) => Ok(course),
        Err(e) => {
            error!("Failed to fetch course: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn create_new_course(
    request: CourseCreateRequest,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    debug!("Creating new course with name: {}", request.name);
    match create_course(request, state).await {
        Ok(course) => {
            debug!("Successfully created course with id: {}", course.id);
            Ok(course)
        }
        Err(e) => {
            error!("Failed to create course: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn update_course_command(
    id: u64,
    request: CourseUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    debug!("Updating course: {}", id);
    match update_course(id, request, state).await {
        Ok(course) => {
            debug!("Successfully updated course: {}", course.id);
            Ok(course)
        }
        Err(e) => {
            error!("Failed to update course: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn delete_course_command(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    debug!("Deleting course: {}", id);
    match delete_course(id, state).await {
        Ok(_) => {
            debug!("Successfully deleted course: {}", id);
            Ok(())
        }
        Err(e) => {
            error!("Failed to delete course: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn upload_course_image_command(
    course_id: u64,
    alt_text: Option<String>,
    app: tauri::AppHandle,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    debug!("Opening file dialog for course image upload: {}", course_id);

    let image_data = handle_image_selection(&app, Some("Select course image"))?;

    match upload_course_image(course_id, image_data, alt_text, state).await {
        Ok(course) => {
            debug!("Successfully uploaded image for course: {}", course_id);
            Ok(course)
        }
        Err(e) => {
            error!("Failed to upload course image: {:?}", e);
            Err(e)
        }
    }
}
