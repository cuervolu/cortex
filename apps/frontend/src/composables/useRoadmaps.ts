import { API_ROUTES } from "@cortex/shared/config/api";
import type { PaginatedRoadmaps, RoadmapDetails, SortQueryParams } from '@cortex/shared/types'
import { AppError } from '@cortex/shared/types';

export function useRoadmaps() {
  const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null);
  const roadmap = ref<RoadmapDetails | null>(null);
  const loading = ref(true);
  const errorHandler = useWebErrorHandler()
  const { token } = useAuth();

  const getFetchOptions = () => ({
    headers: {
      'Authorization': `${token.value}`,
      'Content-Type': 'application/json',
    }
  });

  const fetchRoadmaps = async ({
    page = 0,
    size = 10,
    sort = ['createdAt:desc'],
    isAdmin = false
  }: SortQueryParams = {}) => {
    try {
      loading.value = true;
      const queryParams = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        sort: sort.join(',')
      });

      const url = `${API_ROUTES.ROADMAPS}?${queryParams.toString()}`;
      console.log('Fetching roadmaps:', url);

      const response = await $fetch<PaginatedRoadmaps>(url, getFetchOptions());

      if (!response) {
        throw new AppError('No roadmaps found', {
          statusCode: 404,
          data: { page, size, sort, isAdmin }
        });
      }

      paginatedRoadmaps.value = response;
      return response;
    } catch (err) {
      await errorHandler.handleError(err, {
        statusCode: err instanceof AppError ? err.statusCode : 500,
        fatal: false,
        data: { page, size, sort, isAdmin }
      });
    } finally {
      loading.value = false;
    }
  };

  const fetchRoadmap = async (slug: string) => {
    try {
      loading.value = true;
      console.log(`Fetching roadmap: ${slug}`);

      const response = await $fetch<RoadmapDetails>(
        `${API_ROUTES.ROADMAPS}/${slug}`,
        getFetchOptions()
      );

      if (!response) {
        throw new AppError('Roadmap not found', {
          statusCode: 404,
          data: { slug }
        });
      }

      roadmap.value = response;
      return response;
    } catch (err) {
      await errorHandler.handleError(err, {
        statusCode: err instanceof AppError ? err.statusCode : 500,
        fatal: false,
        data: { slug }
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
    loading
  };
}