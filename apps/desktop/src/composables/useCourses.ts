import {invoke} from '@tauri-apps/api/core'
import {debug, error} from '@tauri-apps/plugin-log'
import {
  AppError,
  type Course,
  type CourseCreateRequest,
  type CourseUpdateRequest, type PaginatedCourses,
} from '@cortex/shared/types'
import TurndownService from 'turndown'

export function useCourses() {
  const courses = ref<PaginatedCourses | null>(null);
  const course = ref<Course | null>(null)
  const loading = ref(false)

  const turndownService = new TurndownService({
    codeBlockStyle: 'fenced',
    headingStyle: 'atx'
  })

  const fetchRoadmapCourses = async (
      roadmapId: number,
      params: { page?: number; size?: number; sort?: string[] } = {}
  ) => {
    try {
      loading.value = true;
      const response = await invoke<PaginatedCourses>('get_roadmap_courses', {
        roadmapId,
        page: params.page,
        size: params.size,
        sort: params.sort
      });
      courses.value = response;
      return response;
    } catch (err) {
      await error(`Failed to fetch roadmap courses: ${err}`);
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const fetchCourse = async (slug: string) => {
    try {
      loading.value = true
      await debug(`Fetching course: ${slug}`)
      const response = await invoke<Course>('get_course', {slug})

      if (!response) {
        throw new Error('Curso no encontrado')
      }

      course.value = response
      await debug(`Successfully fetched course: ${response.name}`)
      return response
    } catch (err) {
      await error(`Failed to fetch course: ${err}`)
      throw err
    } finally {
      loading.value = false
    }
  }

  const createCourse = async (request: CourseCreateRequest, imagePath: string | null, roadmapId: number) => {
    try {
      loading.value = true
      await debug(`Creating course: ${request.name}. Publish: ${request.is_published}`)

      const createdCourse = await invoke<Course>('create_new_course', {
        request: {
          name: request.name,
          description: request.description,
          tags: request.tags,
          is_published: request.is_published,
          display_order: request.display_order
        }
      })

      if (imagePath && createdCourse.id) {
        await debug(`Uploading course image: ${imagePath}`)
        await invoke('upload_course_image_command', {
          courseId: createdCourse.id,
          imagePath,
          altText: request.name
        })
      }

      await debug(`Assigning course ${createdCourse.id} to roadmap ${roadmapId}`)
      await invoke('update_roadmap_command', {
        id: roadmapId,
        request: {
          course_ids: [createdCourse.id]
        }
      })

      await debug(`Successfully created and assigned course: ${createdCourse.id}`)
      return createdCourse
    } catch (err) {
      await error(`Failed to create/assign course: ${err}`)
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateCourse = async (id: number, request: CourseUpdateRequest, imagePath: string | null) => {
    try {
      loading.value = true
      await debug(`Updating course: ${id}. Publish: ${request.is_published}`)

      const updatedCourse = await invoke<Course>('update_course_command', {
        id,
        request
      })

      if (imagePath) {
        await invoke('upload_course_image_command', {
          courseId: id,
          imagePath,
          altText: request.name
        })
      }

      await debug(`Successfully updated course: ${id}`)
      return updatedCourse
    } catch (err) {
      await error(`Failed to update course: ${err}`)
      throw err
    } finally {
      loading.value = false
    }
  }

  interface CourseFormData extends CourseModuleFormValues {
    display_order?: number;
  }

  const transformFormToRequest = (formData: CourseModuleFormValues): CourseCreateRequest => {
    if (!formData.name && !formData.title) {
      throw new AppError('Name is required', {
        statusCode: 400,
        data: { formData }
      });
    }

    return {
      name: formData.name || formData.title || '', // Handle both name and title
      description: turndownService.turndown(formData.description),
      tags: formData.tagNames?.map((name: string) => ({name})),
      is_published: formData.is_published,
      display_order: formData.display_order || 0
    }
  }
  return {
    courses,
    course,
    loading,
    fetchRoadmapCourses,
    fetchCourse,
    createCourse,
    updateCourse,
    transformFormToRequest
  }
}