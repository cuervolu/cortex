export interface ExerciseList {
  id: number;
  slug: string;
  title: string;
  points: number;
  instructions: string;
  hints: string;
  lessonId: number;
  githubPath: string;
  lastGithubSync: string;
  solutionResponses: string[];
}

export interface ExerciseDetail {
  id: number;
  title: string;
  instructions: string;
  hints: string;
  initialCode: string;
  testCode: string;
  contentHash: string;
}

export interface Roadmap {
  id:           number;
  title:        string;
  description:  string;
  slug:         string;
  image_url?:    string;
  tag_names:    string[];
  course_slugs: string[];
  created_at:   Date;
  updated_at?:   Date;
}

export interface RoadmapDetail {
  id:          number;
  title:       string;
  description: string;
  slug:        string;
  courses:     Course[];
  image_url?:   string;
  tag_names?:   string[];
  created_at:  Date;
  updated_at?:  Date;
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
  updatedAt?: string; 
}

export interface Module {
  id: number;
  name: string;
  description: string;
  slug: string;
  courseId: number;
  courseName: string;
  imageUrl?: string;
  lessonIds: number[];
  createdAt: string;
  updatedAt?: string;
}

export interface Course {
  id:            number;
  name:          string;
  description:   string;
  slug:          string;
  image_url:     string;
  roadmap_slugs: string[];
  tag_names?:     string[];
  module_ids?:    number[];
  created_at:    Date;
  updated_at:    Date;
}

export interface RoadmapListResponse {
  roadmaps: Roadmap[];
}

export interface CourseListResponse {
  courses: Course[];
}

export interface ModuleListResponse {
  modules: Module[];
}

export interface LessonListResponse {
  lessons: Lesson[];
}

export interface ExerciseListResponse {
  exercises: ExerciseList[];
}

export interface ExerciseDetailResponse {
  exercise: ExerciseDetail;
}
