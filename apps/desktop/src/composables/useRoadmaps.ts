import {invoke} from '@tauri-apps/api/core'
import {debug, error} from '@tauri-apps/plugin-log'
import type {
  PaginatedRoadmaps,
  RoadmapDetails,
  SortQueryParams,
  RoadmapCreateRequest,
  RoadmapUpdateRequest,
  Roadmap, RoadmapEnrollment
} from '@cortex/shared/types'
import {AppError} from '@cortex/shared/types'
import TurndownService from 'turndown'

export function useRoadmaps() {
  const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null);
  const roadmap = ref<RoadmapDetails | null>(null);
  const enrollments = ref<RoadmapEnrollment[]>([]);
  const loading = ref(true);
  const errorHandler = useDesktopErrorHandler();

  // Inicializar TurndownService para conversión HTML a Markdown
  const turndownService = new TurndownService({
    codeBlockStyle: 'fenced',
    headingStyle: 'atx'
  })

  const fetchEnrollments = async () => {
    try {
      await debug('Fetching enrollments');
      const response = await invoke<RoadmapEnrollment[]>('get_user_enrollments');

      if (!response) {
        throw new AppError('No enrollments found', {
          statusCode: 404,
        });
      }

      enrollments.value = response;
      await debug(`Successfully fetched ${response.length} enrollments`);
      return response;
    } catch (err) {
      await error(`Failed to fetch enrollments: ${err}`);
      await errorHandler.handleError(err, {
        statusCode: err instanceof AppError ? err.statusCode : 500,
        fatal: false,
      });
      return [];
    }
  };

  const fetchRoadmaps = async ({
                                 page = 0,
                                 size = 10,
                                 sort = ['createdAt:desc'],
                                 isAdmin = false,
                                 enrolledOnly = false,
                               }: SortQueryParams & { enrolledOnly?: boolean } = {}) => {
    try {
      loading.value = true;
      await debug(`Fetching roadmaps: page=${page}, size=${size}, sort=${sort.join(',')}, isAdmin=${isAdmin}`);

      const response = await invoke<PaginatedRoadmaps>('fetch_all_roadmaps', {
        page,
        size,
        sort,
        isAdmin,
      });

      if (!response) {
        throw new AppError('No roadmaps found', {
          statusCode: 404,
          data: {page, size, sort, isAdmin},
        });
      }

      if (enrolledOnly) {
        if (!enrollments.value.length) {
          await fetchEnrollments();
        }

        response.content = response.content.filter((roadmap) =>
            enrollments.value.some((enrollment) => enrollment.roadmap_id === roadmap.id),
        );
      }

      paginatedRoadmaps.value = response;
      await debug(`Successfully fetched ${response.content.length} roadmaps`);
      return response;
    } catch (err) {
      await error(`Failed to fetch roadmaps: ${err}`);
      await errorHandler.handleError(err, {
        statusCode: err instanceof AppError ? err.statusCode : 500,
        fatal: false,
        data: {page, size, sort, isAdmin},
      });
    } finally {
      loading.value = false;
    }
  };

  const fetchRoadmap = async (slug: string) => {
    try {
      loading.value = true;
      await debug(`Fetching roadmap: ${slug}`);
      const response = await invoke<RoadmapDetails>('get_roadmap', {slug});

      if (!response) {
        throw new AppError('Roadmap not found', {
          statusCode: 404,
          data: {slug},
        });
      }

      roadmap.value = response;
      await debug(`Successfully fetched roadmap: ${response.title}`);
      return response;
    } catch (err) {
      await error(`Failed to fetch roadmap: ${err}`);
      await errorHandler.handleError(err, {
        statusCode: err instanceof AppError ? err.statusCode : 500,
        fatal: false,
        data: {slug},
      });
    } finally {
      loading.value = false;
    }
  };


  const createRoadmap = async (request: RoadmapCreateRequest, imagePath: string | null) => {
    try {
      loading.value = true
      await debug(`Creating roadmap: ${request.title}. Publish: ${request.is_published}`)

      const createdRoadmap = await invoke<Roadmap>('create_new_roadmap', {request})

      if (imagePath && createdRoadmap.id) {
        await invoke('upload_roadmap_image_command', {
          roadmapId: createdRoadmap.id,
          imagePath,
          altText: request.title
        })
      }

      await debug(`Successfully created roadmap: ${createdRoadmap.id}`)
      return createdRoadmap
    } catch (err) {
      await error(`Failed to create roadmap: ${err}`)
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateRoadmap = async (
      id: number,
      request: RoadmapUpdateRequest,
      imagePath: string | null
  ) => {
    try {
      loading.value = true
      await debug(`Updating roadmap: ${id}. Publish: ${request.is_published}`)

      // Primero actualizamos el roadmap
      const updatedRoadmap = await invoke<Roadmap>('update_roadmap_command', {
        id,
        request
      })

      // Si hay una nueva imagen, la actualizamos
      if (imagePath) {
        await invoke('upload_roadmap_image_command', {
          roadmapId: id,
          imagePath,
          altText: request.title
        })
      }

      await debug(`Successfully updated roadmap: ${id}`)
      return updatedRoadmap
    } catch (err) {
      await error(`Failed to update roadmap: ${err}`)
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteRoadmap = async (id: number) => {
    try {
      loading.value = true;
      await debug(`Deleting roadmap: ${id}`);
      
      await invoke<void>('delete_roadmap_command', { id });
      
      await debug(`Successfully deleted roadmap: ${id}`);
      
      await fetchRoadmaps({
        page: paginatedRoadmaps.value?.number || 0,
        size: paginatedRoadmaps.value?.size || 10,
        isAdmin: true
      });

      return true;
    } catch (err) {
      await error(`Failed to delete roadmap: ${err}`);
      await errorHandler.handleError(err, {
        statusCode: err instanceof AppError ? err.statusCode : 500,
        fatal: false,
        data: { id }
      });
      return false;
    } finally {
      loading.value = false;
    }
  };


  const transformFormToRequest = <T extends RoadmapCreateRequest | RoadmapUpdateRequest>(
      formData: any,
      isUpdate = false
  ): T => {
    const baseRequest = {
      title: formData.title,
      description: turndownService.turndown(formData.description),
      tags: formData.tagNames.map((name: string) => ({name})),
      is_published: formData.is_published,
    };

    if (isUpdate) {
      return baseRequest as T; // For RoadmapUpdateRequest
    }

    return {
      ...baseRequest,
      course_ids: [] // Only for RoadmapCreateRequest
    } as T;
  };

  return {
    paginatedRoadmaps,
    roadmap,
    loading,
    fetchRoadmaps,
    fetchRoadmap,
    createRoadmap,
    updateRoadmap,
    transformFormToRequest,
    deleteRoadmap,
    enrollments
  }
}