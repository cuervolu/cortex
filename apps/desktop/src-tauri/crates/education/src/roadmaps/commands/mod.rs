use crate::roadmaps::*;
use crate::{
    roadmaps, Course, CourseAssignment, PaginatedResponse, PaginatedRoadmaps, Roadmap,
    RoadmapCourseAssignment, RoadmapCreateRequest, RoadmapDetails, RoadmapEnrollmentResponse,
    RoadmapUpdateRequest,
};
use common::handle_content_image_upload;
use common::state::AppState;
use error::AppError;
use log::{debug, error};
use tauri::{AppHandle, State};

#[tauri::command]
pub async fn fetch_all_roadmaps(
    state: State<'_, AppState>,
    page: Option<u32>,
    size: Option<u32>,
    sort: Option<Vec<String>>,
    is_admin: bool,
) -> Result<PaginatedRoadmaps, AppError> {
    debug!("Fetching all roadmaps");
    match fetch_roadmaps(state, page, size, sort, is_admin).await {
        Ok(roadmaps) => Ok(roadmaps),
        Err(e) => {
            log::error!("Failed to fetch roadmaps: {:?}", e);
            Err(e)
        }   
    }
}

#[tauri::command]
pub async fn get_roadmap(
    slug: String,
    state: State<'_, AppState>,
) -> Result<RoadmapDetails, AppError> {
    debug!("Fetching roadmap with slug: {}", slug);
    match fetch_roadmaps_details(&slug, state).await {
        Ok(roadmap) => Ok(roadmap),
        Err(e) => {
            log::error!("Failed to fetch roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn fetch_roadmap_course(
    roadmap_slug: String,
    course_slug: String,
    state: State<'_, AppState>,
) -> Result<Course, AppError> {
    debug!("Fetching course with slug: {} from roadmap with slug: {}", course_slug, roadmap_slug);
    match fetch_course_from_roadmap(&roadmap_slug, &course_slug, state).await {
        Ok(course) => Ok(course),
        Err(e) => {
            log::error!("Failed to fetch course: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn create_new_roadmap(
    request: RoadmapCreateRequest,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    debug!("Creating new roadmap with title: {}", request.title);
    match create_roadmap(request, state).await {
        Ok(roadmap) => {
            debug!("Successfully created roadmap with id: {}", roadmap.id);
            Ok(roadmap)
        }
        Err(e) => {
            log::error!("Failed to create roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn upload_roadmap_image_command(
    roadmap_id: u64,
    image_path: String,
    alt_text: Option<String>,
    app: AppHandle,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    handle_content_image_upload::<Roadmap>(roadmap_id, image_path, alt_text, "roadmap", &app, state)
        .await
}

#[tauri::command]
pub async fn update_roadmap_command(
    id: u64,
    request: RoadmapUpdateRequest,
    state: State<'_, AppState>,
) -> Result<Roadmap, AppError> {
    debug!("Updating roadmap: {}", id);
    match update_roadmap(id, request, state).await {
        Ok(roadmap) => {
            debug!("Successfully updated roadmap: {}", roadmap.id);
            Ok(roadmap)
        }
        Err(e) => {
            log::error!("Failed to update roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn delete_roadmap_command(id: u64, state: State<'_, AppState>) -> Result<(), AppError> {
    debug!("Deleting roadmap: {}", id);
    match delete_roadmap(id, state).await {
        Ok(_) => {
            debug!("Successfully deleted roadmap: {}", id);
            Ok(())
        }
        Err(e) => {
            error!("Failed to delete roadmap: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_roadmap_courses(
    roadmap_id: u64,
    page: Option<u32>,
    size: Option<u32>,
    sort: Option<Vec<String>>,
    state: State<'_, AppState>,
) -> Result<PaginatedResponse<Course>, AppError> {
    debug!("Fetching courses for roadmap: {}", roadmap_id);
    match roadmaps::get_roadmap_courses(roadmap_id, state, page, size, sort).await {
        Ok(paginated_courses) => {
            debug!("Successfully fetched {} courses", paginated_courses.content.len());
            Ok(paginated_courses)
        }
        Err(e) => {
            error!("Failed to fetch roadmap courses: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn get_available_courses(
    roadmap_id: u64,
    page: Option<u32>,
    size: Option<u32>,
    sort: Option<Vec<String>>,
    include_unpublished: Option<bool>,
    state: State<'_, AppState>,
) -> Result<PaginatedResponse<Course>, AppError> {
    debug!("Fetching available courses for roadmap: {}", roadmap_id);
    match roadmaps::get_available_courses(roadmap_id, state, page, size, sort, include_unpublished)
        .await
    {
        Ok(paginated_courses) => {
            debug!("Successfully fetched {} available courses", paginated_courses.content.len());
            Ok(paginated_courses)
        }
        Err(e) => {
            error!("Failed to fetch available courses: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn update_roadmap_courses(
    roadmap_id: u64,
    assignments: Vec<CourseAssignment>,
    state: State<'_, AppState>,
) -> Result<(), AppError> {
    debug!("Updating courses for roadmap: {}", roadmap_id);
    let wrapped_assignments = RoadmapCourseAssignment { assignments };
    match roadmaps::update_roadmap_courses(roadmap_id, wrapped_assignments, state).await {
        Ok(_) => {
            debug!("Successfully updated roadmap courses");
            Ok(())
        }
        Err(e) => {
            error!("Failed to update roadmap courses: {:?}", e);
            Err(e)
        }
    }
}

#[tauri::command]
pub async fn enroll_in_roadmap(
    roadmap_id: u64,
    state: State<'_, AppState>,
) -> Result<RoadmapEnrollmentResponse, AppError> {
    super::enroll_in_roadmap(roadmap_id, state).await
}

#[tauri::command]
pub async fn get_user_enrollments(
    state: State<'_, AppState>,
) -> Result<Vec<RoadmapEnrollmentResponse>, AppError> {
    debug!("Fetching user enrollments");
    match fetch_enrollments(state).await {
        Ok(enrollments) => {
            debug!("Successfully fetched {} enrollments", enrollments.len());
            Ok(enrollments)
        }
        Err(e) => {
            error!("Failed to fetch enrollments: {:?}", e);
            Err(e)
        }
    }
}
