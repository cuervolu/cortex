<script setup lang="ts">

import type {PaginatedRoadmaps} from "@cortex/shared/types";
import RoadmapCard from "~/components/roadmap/RoadmapCard.vue";
import RoadmapPagination from "~/components/roadmap/RoadmapPagination.vue";

interface Props {
  paginatedRoadmaps: PaginatedRoadmaps
}

const props = defineProps<Props>()
const emit = defineEmits(['pageChange'])

const onPageChange = (page: number) => {
  emit('pageChange', page)
}
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-8 text-center">Roadmaps</h1>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <RoadmapCard
          v-for="roadmap in props.paginatedRoadmaps.content"
          :key="roadmap.id"
          :roadmap="roadmap"
      />
    </div>
    <RoadmapPagination
        :current-page="props.paginatedRoadmaps.number + 1"
        :total-pages="props.paginatedRoadmaps.total_pages"
        @page-change="onPageChange"
    />
  </div>
</template>