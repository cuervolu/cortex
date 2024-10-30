<script setup lang="ts">
import type { Roadmap } from '@cortex/shared/types'
import RoadmapList from '@cortex/shared/components/roadmaps/RoadmapList.vue'
import RoadmapCardSkeleton from '@cortex/shared/components/roadmaps/RoadmapCardSkeleton.vue'
import { useRoadmaps } from '~/composables/useRoadmaps'

const router = useRouter()
const { paginatedRoadmaps, loading, fetchRoadmaps } = useRoadmaps()
const sortBy = ref('recent')

const handlePageChange = (page: number) => {
  fetchRoadmaps({ page: page - 1 })
}

const handleSortChange = (sort: string) => {
  sortBy.value = sort
  fetchRoadmaps({ sort })
}

const handleRoadmapClick = (roadmap: Roadmap) => {
  router.push(`/roadmaps/${roadmap.slug}`)
}

onMounted(() => fetchRoadmaps())
</script>

<template>
  <div class="self-stretch m-[30px]">
    <section
        v-if="loading"
        class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full"
    >
      <RoadmapCardSkeleton v-for="i in 6" :key="i" />
    </section>

    <RoadmapList
        v-else-if="paginatedRoadmaps"
        :paginated-roadmaps="paginatedRoadmaps"
        :is-loading="loading"
        :sort-by="sortBy"
        @sort-change="handleSortChange"
        @page-change="handlePageChange"
        @roadmap-click="handleRoadmapClick"
    />
  </div>
</template>