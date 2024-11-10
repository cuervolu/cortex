import {API_ROUTES} from "@cortex/shared/config/api";
import type {PaginatedRoadmaps, RoadmapDetails} from "@cortex/shared/types"

interface RoadmapsResponse {
  data: Ref<PaginatedRoadmaps | null>
  error: Ref<string | null>
  loading: Ref<boolean>
  refresh: () => Promise<RoadmapsResponse>
}

interface RoadmapDetailsResponse {
  data: Ref<RoadmapDetails | null>
  error: Ref<string | null>
  loading: Ref<boolean>
  refresh: () => Promise<RoadmapDetailsResponse>
}

export const useRoadmaps = () => {
  const error = ref<string | null>(null)
  const loading = ref(false)
  const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null)
  const roadmap = ref<RoadmapDetails | null>(null)
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

    loading.value = true
    error.value = null

    try {
      paginatedRoadmaps.value = await $fetch<PaginatedRoadmaps>(url, {
        ...getFetchOptions(),
      })

      return {
        data: paginatedRoadmaps as Ref<PaginatedRoadmaps | null>,
        error: readonly(error),
        loading: readonly(loading),
        refresh: () => fetchRoadmaps(params)
      }
    } catch (e) {
      console.error('Error fetching roadmaps:', e)
      error.value = e instanceof Error ? e.message : 'Error fetching roadmaps'
      paginatedRoadmaps.value = null
      return {
        data: paginatedRoadmaps as Ref<PaginatedRoadmaps | null>,
        error: readonly(error),
        loading: readonly(loading),
        refresh: () => fetchRoadmaps(params)
      }
    } finally {
      loading.value = false
    }
  }

  const getRoadmapDetails = async (slug: string): Promise<RoadmapDetailsResponse> => {
    loading.value = true
    error.value = null

    try {
      roadmap.value = await $fetch<RoadmapDetails>(`${API_ROUTES.ROADMAPS}/${slug}`, {
        ...getFetchOptions(),
      })

      return {
        data: roadmap as Ref<RoadmapDetails | null>,
        error: readonly(error),
        loading: readonly(loading),
        refresh: () => getRoadmapDetails(slug)
      }
    } catch (e) {
      console.error('Error fetching roadmap details:', e)
      error.value = e instanceof Error ? e.message : 'Error fetching roadmap details'
      roadmap.value = null
      return {
        data: roadmap as Ref<RoadmapDetails | null>,
        error: readonly(error),
        loading: readonly(loading),
        refresh: () => getRoadmapDetails(slug)
      }
    } finally {
      loading.value = false
    }
  }

  return {
    fetchRoadmaps,
    getRoadmapDetails,
    paginatedRoadmaps,
    roadmap,
    loading
  }
}