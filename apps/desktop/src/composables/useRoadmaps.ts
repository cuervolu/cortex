import { invoke } from '@tauri-apps/api/core';
import { debug, error } from '@tauri-apps/plugin-log';
import type {
  PaginatedRoadmaps,
  RoadmapDetails,
  SortQueryParams,
  RoadmapEnrollment,
} from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';

export function useRoadmaps() {
  const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null);
  const roadmap = ref<RoadmapDetails | null>(null);
  const enrollments = ref<RoadmapEnrollment[]>([]);
  const loading = ref(true);
  const errorHandler = useDesktopErrorHandler();

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
          data: { page, size, sort, isAdmin },
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
        data: { page, size, sort, isAdmin },
      });
    } finally {
      loading.value = false;
    }
  };

  const fetchRoadmap = async (slug: string) => {
    try {
      loading.value = true;
      await debug(`Fetching roadmap: ${slug}`);
      const response = await invoke<RoadmapDetails>('get_roadmap', { slug });

      if (!response) {
        throw new AppError('Roadmap not found', {
          statusCode: 404,
          data: { slug },
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
        data: { slug },
      });
    } finally {
      loading.value = false;
    }
  };

  return {
    paginatedRoadmaps,
    fetchRoadmaps,
    roadmap,
    fetchRoadmap,
    loading,
    enrollments,
    fetchEnrollments,
  };
}