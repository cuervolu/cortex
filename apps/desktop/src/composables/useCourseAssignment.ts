import {invoke} from '@tauri-apps/api/core'
import {debug} from '@tauri-apps/plugin-log'
import type {Course, Roadmap, PaginatedResponse, SortQueryParams} from '@cortex/shared/types'

export interface AssignmentItem extends Course {
  order: number
  fixed?: boolean
}

interface CourseQueryOptions extends SortQueryParams {
  includeUnpublished?: boolean
}

export function useCourseAssignment() {
  const selectedRoadmap = ref<Roadmap | null>(null)
  const assignedCourses = ref<AssignmentItem[]>([])
  const availableCourses = ref<AssignmentItem[]>([])
  const isLoading = ref(false)

  const assignedCoursesTotal = ref(0)
  const availableCoursesTotal = ref(0)

  const dragOptions = {
    animation: 200,
    group: 'courses',
    disabled: false,
    ghostClass: 'opacity-50'
  }

  const setRoadmap = async (roadmap: Roadmap) => {
    try {
      isLoading.value = true
      selectedRoadmap.value = roadmap
      await loadCourses(roadmap.id)
    } catch (err) {
      await debug(`Error setting roadmap: ${err}`)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const loadCourses = async (
      roadmapId: number,
      options: CourseQueryOptions = {
        page: 0,
        size: 50,
        sort: ['displayOrder:asc'],
        includeUnpublished: true
      }
  ) => {
    try {
      const [assignedResponse, availableResponse] = await Promise.all([
        invoke<PaginatedResponse<Course>>('get_roadmap_courses', {
          roadmapId,
          page: options.page,
          size: options.size,
          sort: options.sort
        }),
        invoke<PaginatedResponse<Course>>('get_available_courses', {
          roadmapId,
          page: options.page,
          size: options.size,
          sort: options.sort,
          includeUnpublished: options.includeUnpublished
        })
      ]);

      assignedCoursesTotal.value = assignedResponse.total_elements;
      availableCoursesTotal.value = availableResponse.total_elements;

      assignedCourses.value = assignedResponse.content.map((course, index) => ({
        ...course,
        order: index,
      }));

      availableCourses.value = availableResponse.content.map((course, index) => ({
        ...course,
        order: index,
      }));
    } catch (err) {
      await debug(`Error loading courses: ${err}`);
      throw err;
    }
  }

  // If necessary, load more courses from the API
  const loadMoreCourses = async (type: 'assigned' | 'available') => {
    if (!selectedRoadmap.value) return

    const currentItems = type === 'assigned' ? assignedCourses.value : availableCourses.value
    const totalItems = type === 'assigned' ? assignedCoursesTotal.value : availableCoursesTotal.value

    if (currentItems.length >= totalItems) return

    const page = Math.floor(currentItems.length / 50)

    try {
      isLoading.value = true
      await loadCourses(selectedRoadmap.value.id, {
        page,
        size: 50,
        sort: ['displayOrder:asc'],
        includeUnpublished: true
      })
    } catch (err) {
      await debug(`Error loading more courses: ${err}`)
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const saveCourseAssignments = async () => {
    if (!selectedRoadmap.value) return;

    try {
      isLoading.value = true;
      const assignments = assignedCourses.value.map((course, index) => ({
        course_id: course.id,
        display_order: index
      }));

      await invoke('update_roadmap_courses', {
        roadmapId: selectedRoadmap.value.id,
        assignments
      });

      await debug('Course assignments saved successfully');
      await loadCourses(selectedRoadmap.value.id);
    } catch (err) {
      await debug(`Error saving course assignments: ${err}`);
      throw err;
    } finally {
      isLoading.value = false;
    }
  };

  return {
    selectedRoadmap,
    assignedCourses,
    availableCourses,
    isLoading,
    assignedCoursesTotal,
    availableCoursesTotal,
    dragOptions,
    setRoadmap,
    loadMoreCourses,
    saveCourseAssignments
  }
}