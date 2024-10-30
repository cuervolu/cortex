import { ref } from 'vue'
import { invoke } from '@tauri-apps/api/core'
import { debug, info } from '@tauri-apps/plugin-log'
import type { PaginatedRoadmaps, RoadmapDetails } from '@cortex/shared/types'

export interface FetchRoadmapsParams {
  page?: number
  size?: number
  sort?: string
}

export function useRoadmaps() {
  const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null)
  const roadmap = ref<RoadmapDetails | null>(null)
  const loading = ref(true)
  const { handleError, createAppError } = useErrorHandler()

  const fetchRoadmaps = async ({ page = 0, size = 10, sort = 'recent' }: FetchRoadmapsParams = {}) => {
    try {
      loading.value = true
      await debug(`Fetching roadmaps: page=${page}, size=${size}, sort=${sort}`)

      const response = await invoke<PaginatedRoadmaps>('fetch_all_roadmaps', {
        page,
        size,
        sort
      })

      if (!response) {
        throw createAppError('No roadmaps found', {
          statusCode: 404,
          data: { page, size, sort }
        })
      }

      paginatedRoadmaps.value = response
      await info(`Successfully fetched ${response.content.length} roadmaps`)

      return response
    } catch (err) {
      const isInvokeError = err instanceof Error &&
          err.message.includes('invoke')

      await handleError(err, {
        statusCode: isInvokeError ? 500 : 404,
        fatal: isInvokeError,
        data: { page, size, sort },
        cause: err
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
        throw createAppError('Roadmap not found', {
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

      await handleError(err, {
        statusCode: isInvokeError ? 500 : 404,
        fatal: isInvokeError,
        data: { slug },
        cause: err
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