use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize)]
pub struct ExerciseList {
    pub id: u32,
    pub slug: String,
    pub title: String,
    pub points: u32,
    pub instructions: String,
    pub hints: String,
    pub lesson_id: u32,
    pub github_path: String,
    pub last_github_sync: String,
    pub solution_responses: Vec<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct ExerciseDetail {
    pub id: u32,
    pub title: String,
    pub instructions: String,
    pub hints: String,
    pub initial_code: String,
    pub test_code: String,
    pub lesson_name: String,
    pub file_name: String,
    pub language: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Roadmap {
    pub id: u32,
    pub title: String,
    pub description: String,
    pub image_url: Option<String>,
    pub slug: String,
    pub tag_names: Vec<String>,
    pub course_slugs: Vec<String>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Lesson {
    pub id: u32,
    pub name: String,
    pub content: String,
    pub credits: u32,
    pub slug: String,
    pub module_id: u32,
    pub module_name: String,
    pub exercise_ids: Vec<u32>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Module {
    pub id: u32,
    pub name: String,
    pub description: String,
    pub slug: String,
    pub course_id: u32,
    pub course_name: String,
    pub image_url: Option<String>,
    pub lesson_ids: Vec<u32>,
    pub created_at: String,
    pub updated_at: Option<String>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct RoadmapDetail {
    pub id: i64,
    pub title: String,
    pub description: String,
    pub slug: String,
    pub courses: Vec<Course>,
    pub image_url: String,
    pub tag_names: Option<serde_json::Value>,
    pub created_at: String,
    pub updated_at: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Course {
    pub id: i64,
    pub name: String,
    pub description: String,
    pub slug: String,
    pub image_url: String,
    pub roadmap_slugs: Option<serde_json::Value>,
    pub tag_names: Vec<Option<serde_json::Value>>,
    pub module_ids: Option<serde_json::Value>,
    pub created_at: String,
    pub updated_at: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct RoadmapListResponse(pub Vec<Roadmap>);

#[derive(Debug, Serialize, Deserialize)]
pub struct CourseListResponse(pub Vec<Course>);

#[derive(Debug, Serialize, Deserialize)]
pub struct ModuleListResponse(pub Vec<Module>);

#[derive(Debug, Serialize, Deserialize)]
pub struct LessonListResponse(pub Vec<Lesson>);

#[derive(Debug, Serialize, Deserialize)]
pub struct ExerciseListResponse(pub Vec<ExerciseList>);

#[derive(Debug, Serialize, Deserialize)]
pub struct ExerciseDetailResponse {
    pub exercise: ExerciseDetail,
}
