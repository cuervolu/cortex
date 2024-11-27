import { invoke } from '@tauri-apps/api/core'
import { debug, error } from '@tauri-apps/plugin-log'
import type { Course, CourseCreateRequest } from '@cortex/shared/types'
import { AppError } from '@cortex/shared/types'
import TurndownService from 'turndown'

export function useCourses() {
  const course = ref<Course | null>(null)
  const loading = ref(false)
  const errorHandler = useDesktopErrorHandler()

  const turndownService = new TurndownService({
    codeBlockStyle: 'fenced',
    headingStyle: 'atx'
  })

  const createCourse = async (request: CourseCreateRequest, imagePath: string | null) => {
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

      await debug(`Successfully created course: ${createdCourse.id}`)
      return createdCourse
    } catch (err) {
      await error(`Failed to create course: ${err}`)
      throw err
    } finally {
      loading.value = false
    }
  }

  interface CourseFormData extends CourseModuleFormValues {
    display_order?: number;
  }

  const transformFormToRequest = (formData: CourseModuleFormValues): CourseCreateRequest => {
    return {
      name: formData.name || '',
      description: turndownService.turndown(formData.description),
      tags: formData.tagNames?.map((name: string) => ({ name })),
      is_published: formData.is_published,
      display_order: formData.display_order || 0
    }
  }
  return {
    course,
    loading,
    createCourse,
    transformFormToRequest
  }
}