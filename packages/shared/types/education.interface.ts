export interface PaginatedResponse<T> {
  content: T[];
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  total_elements: number;
  total_pages: number;
}

export interface SubmissionResponse {
  id: number;
  code: string;
  languageId: number;
  stdin: string;
  expectedOutput: string;
  solutionId: number;
}

export interface SolutionResponse {
  id: number;
  userId: number;
  exerciseId: number;
  status: number;
  pointsEarned: number;
  submissions: SubmissionResponse[];
}

export interface Exercise {
  id: number;
  slug: string;
  title: string;
  points: number;
  instructions: string;
  hints: string;
  lesson_id: number;
  github_path: string;
  last_github_sync: string;
  solution_responses: SolutionResponse[];
}

export interface Lesson {
  id: number;
  name: string;
  content: string;
  credits: number;
  slug: string;
  moduleId: number;
  moduleName: string;
  exerciseIds: number[];
  createdAt: string;
  updatedAt: string | null;
}

export interface Roadmap {
  id: number;
  title: string;
  description: string;
  slug: string;
  image_url: string | null;
  tag_names: string[];
  is_published: boolean;
  course_slugs: string[];
  created_at: string;
  updated_at: string | null;
}

export interface Module {
  id: number;
  name: string;
  description: string;
  slug: string;
  courseId: number;
  courseName: string;
  imageUrl: string | null;
  lessonIds: number[];
  createdAt: string;
  updatedAt: string | null;
}

export interface ExerciseDetails extends Exercise {
  language: string;
  initial_code: string;
  test_code: string;
  lesson_name: string;
  file_name: string;
}

export interface Course {
  id: number;
  name: string;
  description: string;
  slug: string;
  image_url: string | null;
  roadmap_slugs: string[];
  tag_names: string[];
  module_ids: number[];
  created_at: string;
  updated_at: string | null;
}

export interface RoadmapLesson {
  id: number;
  name: string;
  slug: string;
  credits: number;
}

export interface RoadmapModule {
  id: number;
  name: string;
  description: string;
  slug: string;
  lesson_count: number;
  lessons: RoadmapLesson[];
}

export interface RoadmapCourse {
  id: number;
  name: string;
  description: string;
  slug: string;
  image_url: string | null;
  tag_names: string[];
  modules: RoadmapModule[];
}

export interface RoadmapDetails {
  id: number;
  title: string;
  description: string;
  slug: string;
  image_url: string | null;
  tag_names: string[];
  is_published: boolean;
  courses: RoadmapCourse[];
  created_at: string;
  updated_at: string | null;
}



export type PaginatedExercises = PaginatedResponse<Exercise>;
export type PaginatedLessons = PaginatedResponse<Lesson>;
export type PaginatedRoadmaps = PaginatedResponse<Roadmap>;
export type PaginatedModules = PaginatedResponse<Module>;
export type PaginatedCourses = PaginatedResponse<Course>;
