pub mod courses;
pub mod modules;
pub mod exercises;
pub mod lessons;
pub mod roadmaps;

use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct PaginatedResponse<T> {
    pub content: Vec<T>,
    pub number: u32,
    pub size: u32,
    pub first: bool,
    pub last: bool,
    pub total_elements: u64,
    pub total_pages: u32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct SubmissionResponse {
    pub id: u64,
    pub code: String,
    #[serde(default)]
    pub stdin: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub language_id: Option<u64>,
    #[serde(default)]
    pub expected_output: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub solution_id: Option<u64>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct SolutionResponse {
    pub id: u64,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub user_id: Option<u64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub exercise_id: Option<u64>,
    pub status: u32,
    pub points_earned: u32,
    pub submissions: Vec<SubmissionResponse>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Exercise {
    pub id: u64,
    pub slug: String,
    pub title: String,
    pub points: u32,
    pub instructions: String,
    pub hints: String,
    pub lesson_id: u64,
    pub github_path: String,
    pub last_github_sync: String,
    pub solution_responses: Vec<SolutionResponse>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Lesson {
    pub id: u64,
    pub name: String,
    pub content: String,
    pub credits: u32,
    pub slug: String,
    pub module_id: u64,
    pub module_name: String,
    pub exercise_ids: Vec<u64>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Roadmap {
    pub id: u64,
    pub title: String,
    pub description: String,
    pub slug: String,
    pub image_url: Option<String>,
    pub tag_names: Vec<String>,
    pub is_published: bool,
    pub course_slugs: Vec<String>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Module {
    pub id: u64,
    pub name: String,
    pub description: String,
    pub slug: String,
    pub course_id: u64,
    pub course_name: String,
    pub image_url: Option<String>,
    pub lesson_ids: Vec<u64>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ExerciseDetails {
    pub id: u64,
    pub title: String,
    pub instructions: String,
    pub hints: String,
    pub language: String,
    pub initial_code: String,
    pub test_code: String,
    pub lesson_name: String,
    pub file_name: String,
    pub slug: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Course {
    pub id: u64,
    pub name: String,
    pub description: String,
    pub slug: String,
    pub image_url: Option<String>,
    pub roadmap_slugs: Vec<String>,
    pub tag_names: Vec<String>,
    pub module_ids: Vec<u64>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapDetails {
    pub id: u64,
    pub title: String,
    pub description: String,
    pub slug: String,
    pub courses: Vec<Course>,
    pub image_url: Option<String>,
    pub tag_names: Vec<String>,
    pub is_published: bool,
    pub created_at: String,
    pub updated_at: Option<String>,
}


// Exercise Submission
#[derive(Debug, Serialize, Deserialize)]
pub struct CodeExecutionRequest {
    pub code: String,
    pub language: String,
    #[serde(rename = "exercise_id")]
    pub exercise_id: u64,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct CodeExecutionSubmissionResponse {
    #[serde(rename = "task_id")]
    pub task_id: String,
    pub status: String,
    pub message: String,
    #[serde(rename = "submission_time")]
    pub submission_time: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct TestCaseResult {
    pub passed: bool,
    #[serde(default)] 
    pub input: Option<String>,
    #[serde(rename = "expected_output")]
    pub expected_output: Option<String>,  
    #[serde(rename = "actual_output")]
    pub actual_output: Option<String>,   
    pub message: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct CodeExecutionResult {
    pub success: bool,
    pub stdout: String,
    pub stderr: String,
    #[serde(rename = "execution_time")]
    pub execution_time: i32,
    pub language: String,
    #[serde(rename = "exercise_id")]
    pub exercise_id: u64,
    #[serde(rename = "memory_used")]
    pub memory_used: i32,
    #[serde(rename = "test_case_results")]
    pub test_case_results: Vec<TestCaseResult>,
}



// Paginated Responses
pub type PaginatedExercises = PaginatedResponse<Exercise>;
pub type PaginatedLessons = PaginatedResponse<Lesson>;
pub type PaginatedRoadmaps = PaginatedResponse<Roadmap>;
pub type PaginatedModules = PaginatedResponse<Module>;
pub type PaginatedCourses = PaginatedResponse<Course>;