import { ref, readonly } from 'vue'
import { API_ROUTES } from '~/config/api'
import type { PaginatedRoadmaps, RoadmapDetails } from "@cortex/shared/types";

export const useRoadmaps = () => {
  const error = ref<string | null>(null)
  const isLoading = ref(false)
  const data = ref<PaginatedRoadmaps | null>(null)
  const { token } = useAuth()

  const getFetchOptions = () => ({
    headers: {
      'Authorization': `${token.value}`,
      'Content-Type': 'application/json',
    }
  })

  const fetchRoadmaps = async (params?: {
    page?: number
    size?: number
    sort?: string
  }) => {
    const queryParams = new URLSearchParams()
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())
    if (params?.sort !== undefined) queryParams.append('sort', params.sort)

    const url = `${API_ROUTES.ROADMAPS}?${queryParams.toString()}`
    console.log('Fetching roadmaps:', url)

    isLoading.value = true
    error.value = null

    try {
      data.value = await $fetch<PaginatedRoadmaps>(url, {
        ...getFetchOptions(),
      })

      return {
        data: readonly(data),
        error: readonly(error),
        isLoading: readonly(isLoading),
        refresh: () => fetchRoadmaps(params)
      }
    } catch (e) {
      console.error('Error fetching roadmaps:', e)
      error.value = e instanceof Error ? e.message : 'Error fetching roadmaps'
      return {
        data: readonly(data),
        error: readonly(error),
        isLoading: readonly(isLoading),
        refresh: () => fetchRoadmaps(params)
      }
    } finally {
      isLoading.value = false
    }
  }

  const getRoadmapDetails = async (slug: string) => {
    isLoading.value = true
    error.value = null

    try {
      const response = await $fetch<RoadmapDetails>(`${API_ROUTES.ROADMAPS}/${slug}`, {
        ...getFetchOptions(),
      })

      data.value = response
      return {
        data: readonly(ref(response)),
        error: readonly(error),
        isLoading: readonly(isLoading),
        refresh: () => getRoadmapDetails(slug)
      }
    } catch (e) {
      console.error('Error fetching roadmap details:', e)
      error.value = e instanceof Error ? e.message : 'Error fetching roadmap details'
      return {
        data: readonly(ref(null)),
        error: readonly(error),
        isLoading: readonly(isLoading),
        refresh: () => getRoadmapDetails(slug)
      }
    } finally {
      isLoading.value = false
    }
  }

  return {
    fetchRoadmaps,
    getRoadmapDetails,
  }
}