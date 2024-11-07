import {API_ROUTES} from "@cortex/shared/config/api";
import type {PaginatedRoadmaps, RoadmapDetails} from "@cortex/shared/types"

interface RoadmapsResponse {
  data: Ref<PaginatedRoadmaps | null>
  error: Ref<string | null>
  isLoading: Ref<boolean>
  refresh: () => Promise<RoadmapsResponse>
}

interface RoadmapDetailsResponse {
  data: Ref<RoadmapDetails | null>
  error: Ref<string | null>
  isLoading: Ref<boolean>
  refresh: () => Promise<RoadmapDetailsResponse>
}

export const useRoadmaps = () => {
  const error = ref<string | null>(null)
  const isLoading = ref(false)
  const roadmapsData = ref<PaginatedRoadmaps | null>(null)
  const roadmapDetailsData = ref<RoadmapDetails | null>(null)
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
  }): Promise<RoadmapsResponse> => {
    const queryParams = new URLSearchParams()
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())
    if (params?.sort !== undefined) queryParams.append('sort', params.sort)

    const url = `${API_ROUTES.ROADMAPS}?${queryParams.toString()}`
    console.log('Fetching roadmaps:', url)

    isLoading.value = true
    error.value = null

    try {
      roadmapsData.value = await $fetch<PaginatedRoadmaps>(url, {
        ...getFetchOptions(),
      })

      return {
        data: roadmapsData as Ref<PaginatedRoadmaps | null>,
        error: readonly(error),
        isLoading: readonly(isLoading),
        refresh: () => fetchRoadmaps(params)
      }
    } catch (e) {
      console.error('Error fetching roadmaps:', e)
      error.value = e instanceof Error ? e.message : 'Error fetching roadmaps'
      roadmapsData.value = null
      return {
        data: roadmapsData as Ref<PaginatedRoadmaps | null>,
        error: readonly(error),
        isLoading: readonly(isLoading),
        refresh: () => fetchRoadmaps(params)
      }
    } finally {
      isLoading.value = false
    }
  }

  const getRoadmapDetails = async (slug: string): Promise<RoadmapDetailsResponse> => {
    isLoading.value = true
    error.value = null

    try {
      roadmapDetailsData.value = await $fetch<RoadmapDetails>(`${API_ROUTES.ROADMAPS}/${slug}`, {
        ...getFetchOptions(),
      })

      return {
        data: roadmapDetailsData as Ref<RoadmapDetails | null>,
        error: readonly(error),
        isLoading: readonly(isLoading),
        refresh: () => getRoadmapDetails(slug)
      }
    } catch (e) {
      console.error('Error fetching roadmap details:', e)
      error.value = e instanceof Error ? e.message : 'Error fetching roadmap details'
      roadmapDetailsData.value = null
      return {
        data: roadmapDetailsData as Ref<RoadmapDetails | null>,
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