import { API_ROUTES } from "@cortex/shared/config/api";
import type { PaginatedRoadmaps, RoadmapDetails } from "@cortex/shared/types";
import { AppError } from '@cortex/shared/types';

export function useRoadmaps() {
  const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null);
  const roadmap = ref<RoadmapDetails | null>(null);
  const loading = ref(true);
  const error = ref<Error | null>(null);
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
    sort = ['createdAt:desc']
  } = {}) => {
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
          data: { page, size, sort }
        });
      }

      paginatedRoadmaps.value = response;
      return response;
    } catch (err) {
      console.error('Error fetching roadmaps:', err);
      error.value = err instanceof Error ? err : new Error('Error fetching roadmaps');
      throw error.value;
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
      console.error('Error fetching roadmap:', err);
      error.value = err instanceof Error ? err : new Error('Error fetching roadmap');
      throw error.value;
    } finally {
      loading.value = false;
    }
  };

  return {
    paginatedRoadmaps: paginatedRoadmaps,
    fetchRoadmaps,
    roadmap: roadmap,
    fetchRoadmap,
    loading: loading,
    error: error
  };
}