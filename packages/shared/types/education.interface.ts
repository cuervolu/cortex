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
  completed: boolean;
  display_order?: number;
}

export interface Lesson {
  id: number;
  name: string;
  content: string;
  credits: number;
  slug: string;
  module_id: number;
  module_name: string;
  exercises: ExerciseInfo[];
  created_at: string;
  updated_at: string | null;
  display_order?: number;
  is_published: boolean;
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

export interface ExerciseInfo {
  id: number;
  slug: string;
  title: string;
  is_completed: boolean;
  points: number;
  display_order?: number;
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
  is_published: boolean;
  display_order?: number;
}

export interface RoadmapLesson {
  id: number;
  name: string;
  slug: string;
  credits: number;
  exercises: Exercise[];
  display_order?: number;
}

export interface RoadmapModule {
  id: number;
  name: string;
  description: string;
  slug: string;
  lesson_count: number;
  lessons: RoadmapLesson[];
  display_order?: number;
}

export interface RoadmapCourse {
  id: number;
  name: string;
  description: string;
  slug: string;
  image_url: string | null;
  tag_names: string[];
  modules: RoadmapModule[];
  display_order?: number;
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
  mentor: RoadmapMentor[] | null;
}

export interface CourseDetails extends Omit<Course, 'module_ids'> {
  modules: Module[];
}


export interface TagDTO {
  name: string;
  description?: string;
}

export interface RoadmapCreateRequest {
  title: string;
  description: string;
  tags?: TagDTO[];
  course_ids?: number[];
  is_published: boolean;
}

export interface CourseCreateRequest {
  name: string;
  description: string;
  tags?: TagDTO[];
  is_published: boolean;
  display_order?: number;
}

export interface ModuleCreateRequest {
  course_id: number;
  name: string;
  description: string;
  is_published: boolean;
  display_order?: number;
}

export interface LessonCreateRequest {
  module_id: number;
  name: string;
  content: string;
  credits: number;
  is_published: boolean;
  display_order?: number;
}

export interface RoadmapUpdateRequest {
  title?: string;
  description?: string;
  tags?: TagDTO[];
  course_ids?: number[];
  is_published?: boolean;
}

export interface CourseUpdateRequest {
  name?: string;
  description?: string;
  tags?: TagDTO[];
  is_published?: boolean;
  display_order?: number;
}

export interface ModuleUpdateRequest {
  course_id?: number;
  name?: string;
  description?: string;
  is_published?: boolean;
  display_order?: number;
}

export interface LessonUpdateRequest {
  module_id?: number;
  name?: string;
  content?: string;
  credits?: number;
  is_published?: boolean;
  display_order?: number;
}

export interface ImageUploadRequest {
  image: Uint8Array;
  alt_text?: string;
}

export interface RoadmapEnrollment {
  status: string;
  progress: number;
  user_id: number;
  roadmap_id: number;
  enrollment_date: string;
  last_activity_date: string;
}

export interface RoadmapMentor {
  username: string;
  full_name: string;
  avatar_url: string | null;
}

export type PaginatedExercises = PaginatedResponse<Exercise>;
export type PaginatedLessons = PaginatedResponse<Lesson>;
export type PaginatedRoadmaps = PaginatedResponse<Roadmap>;
export type PaginatedModules = PaginatedResponse<Module>;
export type PaginatedCourses = PaginatedResponse<Course>;
