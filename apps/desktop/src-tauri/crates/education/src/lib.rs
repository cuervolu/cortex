pub mod courses;
pub mod exercises;
pub mod lessons;
pub mod modules;
pub mod roadmaps;

use chrono::{DateTime, Utc};
use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize, Clone, PartialEq)]
#[serde(rename_all = "SCREAMING_SNAKE_CASE")]
pub enum EnrollmentStatus {
    Active,
    Paused,
    Dropped,
    Completed,
}

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
    pub exercises: Vec<ExerciseInfo>,
    pub created_at: String,
    pub updated_at: Option<String>,
    #[serde(rename = "display_order")]
    pub display_order: u32,
    #[serde(rename = "is_published")]
    pub is_published: bool,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ExerciseInfo {
    pub id: u64,
    pub slug: String,
    pub title: String,
    #[serde(rename = "is_completed")]
    pub is_completed: bool,
    pub points: u32,
    #[serde(rename = "display_order")]
    pub display_order: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Roadmap {
    pub id: u64,
    pub title: String,
    pub description: String,
    pub slug: String,
    pub image_url: Option<String>,
    pub tag_names: Option<Vec<String>>,
    pub is_published: bool,
    pub course_slugs: Option<Vec<String>>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapEnrollmentResponse {
    #[serde(rename = "user_id")]
    pub user_id: u64,

    #[serde(rename = "roadmap_id")]
    pub roadmap_id: u64,

    #[serde(rename = "enrollment_date")]
    #[serde(with = "datetime_format")]
    pub enrollment_date: DateTime<Utc>,

    pub status: EnrollmentStatus,

    #[serde(rename = "last_activity_date")]
    #[serde(with = "datetime_format")]
    pub last_activity_date: DateTime<Utc>,

    pub progress: f64,
}

mod datetime_format {
    use chrono::{DateTime, NaiveDateTime, Utc};
    use serde::{self, Deserialize, Deserializer, Serializer};

    const FORMAT: &str = "%Y-%m-%dT%H:%M:%S%.f";

    pub fn serialize<S>(date: &DateTime<Utc>, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: Serializer,
    {
        let s = format!("{}", date.format(FORMAT));
        serializer.serialize_str(&s)
    }

    pub fn deserialize<'de, D>(deserializer: D) -> Result<DateTime<Utc>, D::Error>
    where
        D: Deserializer<'de>,
    {
        let s = String::deserialize(deserializer)?;
        let naive = NaiveDateTime::parse_from_str(&s, FORMAT).map_err(serde::de::Error::custom)?;
        Ok(DateTime::<Utc>::from_naive_utc_and_offset(naive, Utc))
    }
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
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub roadmap_slugs: Option<Vec<String>>,
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub tag_names: Option<Vec<String>>,
    #[serde(default, skip_serializing_if = "Option::is_none")]
    pub module_ids: Option<Vec<u64>>,
    #[serde(rename = "display_order")]
    pub display_order: i32,
    pub created_at: String,
    pub updated_at: Option<String>,
    pub is_published: bool,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct CourseAssignment {
    #[serde(rename = "course_id")]
    pub course_id: u64,
    #[serde(rename = "display_order")]
    pub display_order: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapCourseAssignment {
    pub assignments: Vec<CourseAssignment>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapExercise {
    pub id: u64,
    pub title: String,
    pub slug: String,
    pub points: u32,
    pub completed: bool,
    #[serde(rename = "display_order")]
    pub order: u32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapLesson {
    pub id: u64,
    pub name: String,
    pub slug: String,
    pub exercises: Option<Vec<RoadmapExercise>>,
    pub credits: u32,
    pub display_order: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapModule {
    pub id: u64,
    pub name: String,
    pub description: String,
    pub slug: String,
    #[serde(rename = "lesson_count")]
    pub lesson_count: u32,
    pub lessons: Vec<RoadmapLesson>,
    pub display_order: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapCourse {
    pub id: u64,
    pub name: String,
    pub description: String,
    pub slug: String,
    #[serde(rename = "image_url")]
    pub image_url: Option<String>,
    #[serde(rename = "tag_names")]
    pub tag_names: Vec<String>,
    pub modules: Vec<RoadmapModule>,
    pub display_order: i32,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapMentor {
    pub full_name: String,
    pub username: String,
    pub avatar_url: Option<String>,
}


#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapDetails {
    pub id: u64,
    pub title: String,
    pub description: String,
    pub slug: String,
    #[serde(rename = "image_url")]
    pub image_url: Option<String>,
    #[serde(rename = "tag_names")]
    pub tag_names: Vec<String>,
    #[serde(rename = "is_published")]
    pub is_published: bool,
    pub courses: Vec<RoadmapCourse>,
    pub mentor: RoadmapMentor,
    #[serde(rename = "created_at")]
    pub created_at: String,
    #[serde(rename = "updated_at")]
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

#[derive(Debug, Serialize, Deserialize)]
pub struct TagDTO {
    pub name: String,
    #[serde(skip_serializing_if = "Option::is_none")]
    pub description: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapCreateRequest {
    pub title: String,
    pub description: String,
    pub tags: Option<Vec<TagDTO>>,
    #[serde(rename = "course_ids")]
    pub course_ids: Option<Vec<u64>>,
    #[serde(rename = "is_published")]
    pub is_published: bool,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct CourseCreateRequest {
    pub name: String,
    pub description: String,
    pub tags: Option<Vec<TagDTO>>,
    #[serde(rename = "is_published")]
    pub is_published: bool,
    #[serde(rename = "display_order")]
    pub display_order: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ModuleCreateRequest {
    #[serde(rename = "course_id")]
    pub course_id: u64,
    pub name: String,
    pub description: String,
    #[serde(rename = "is_published")]
    pub is_published: bool,
    #[serde(rename = "display_order")]
    pub display_order: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct LessonCreateRequest {
    #[serde(rename = "module_id")]
    pub module_id: u64,
    pub name: String,
    pub content: String,
    pub credits: i32,
    #[serde(rename = "is_published")]
    pub is_published: bool,
    #[serde(rename = "display_order")]
    pub display_order: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapUpdateRequest {
    pub title: Option<String>,
    pub description: Option<String>,
    pub tags: Option<Vec<TagDTO>>,
    #[serde(rename = "course_ids")]
    pub course_ids: Option<Vec<u64>>,
    #[serde(rename = "is_published")]
    pub is_published: Option<bool>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct CourseUpdateRequest {
    pub name: Option<String>,
    pub description: Option<String>,
    pub tags: Option<Vec<TagDTO>>,
    #[serde(rename = "is_published")]
    pub is_published: Option<bool>,
    #[serde(rename = "display_order")]
    pub display_order: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ModuleUpdateRequest {
    #[serde(rename = "course_id")]
    pub course_id: Option<u64>,
    pub name: Option<String>,
    pub description: Option<String>,
    #[serde(rename = "is_published")]
    pub is_published: Option<bool>,
    #[serde(rename = "display_order")]
    pub display_order: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct LessonUpdateRequest {
    #[serde(rename = "module_id")]
    pub module_id: Option<u64>,
    pub name: Option<String>,
    pub content: Option<String>,
    pub credits: Option<i32>,
    #[serde(rename = "is_published")]
    pub is_published: Option<bool>,
    #[serde(rename = "display_order")]
    pub display_order: Option<i32>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ImageUploadRequest {
    pub image: Vec<u8>,
    #[serde(rename = "alt_text")]
    pub alt_text: Option<String>,
}

// Paginated Responses
pub type PaginatedExercises = PaginatedResponse<Exercise>;
pub type PaginatedLessons = PaginatedResponse<Lesson>;
pub type PaginatedRoadmaps = PaginatedResponse<Roadmap>;
pub type PaginatedModules = PaginatedResponse<Module>;
pub type PaginatedCourses = PaginatedResponse<Course>;
