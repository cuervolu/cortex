import { invoke } from '@tauri-apps/api/core'
import { debug, info } from '@tauri-apps/plugin-log'
import type { PaginatedRoadmaps, RoadmapDetails, SortQueryParams } from '@cortex/shared/types'
import { AppError } from '@cortex/shared/types'

export function useRoadmaps() {
  const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null)
  const roadmap = ref<RoadmapDetails | null>(null)
  const loading = ref(true)
  const errorHandler = useDesktopErrorHandler()

  const fetchRoadmaps = async ({
    page = 0,
    size = 10,
    sort = ['createdAt:desc'],
    isAdmin = false
  }: SortQueryParams = {}) => {
    try {
      loading.value = true
      await debug(`Fetching roadmaps: page=${page}, size=${size}, sort=${sort.join(',')}, isAdmin=${isAdmin}`)

      const response = await invoke<PaginatedRoadmaps>('fetch_all_roadmaps', {
        page,
        size,
        sort,
        isAdmin
      })

      if (!response) {
        throw new AppError('No roadmaps found', {
          statusCode: 404,
          data: { page, size, sort, isAdmin }
        })
      }

      paginatedRoadmaps.value = response
      await info(`Successfully fetched ${response.content.length} roadmaps`)
      return response
    } catch (err) {
      const isInvokeError = err instanceof Error &&
        err.message.includes('invoke')
      await errorHandler.handleError(err, {
        statusCode: isInvokeError ? 500 : 404,
        fatal: isInvokeError,
        data: { page, size, sort, isAdmin }
      })
    } finally {
      loading.value = false
    }
  }

  const fetchRoadmap = async (slug: string) => {
    try {
      loading.value = true
      await debug(`Fetching roadmap: ${slug}`)
      const response = await invoke<RoadmapDetails>('get_roadmap', { slug })
      if (!response) {
        throw new AppError('Roadmap not found', {
          statusCode: 404,
          data: { slug }
        })
      }
      roadmap.value = response
      await info(`Successfully fetched roadmap: ${response.title}`)
      return response
    } catch (err) {
      const isInvokeError = err instanceof Error &&
        err.message.includes('invoke')
      await errorHandler.handleError(err, {
        statusCode: isInvokeError ? 500 : 404,
        fatal: isInvokeError,
        data: { slug }
      })
    } finally {
      loading.value = false
    }
  }

  return {
    paginatedRoadmaps,
    fetchRoadmaps,
    roadmap,
    fetchRoadmap,
    loading
  }
}