<script setup lang="ts">
import type { PaginatedRoadmaps, Roadmap } from "@cortex/shared/types"
import RoadmapPagination from "./RoadmapPagination.vue"
import RoadmapCard from "./RoadmapCard.vue"

const props = defineProps<{
  paginatedRoadmaps: PaginatedRoadmaps
  isLoading?: boolean
  sortBy?: string
}>()

const emit = defineEmits<{
  (e: 'sortChange', value: string): void
  (e: 'pageChange', page: number): void
  (e: 'roadmapClick', roadmap: Roadmap): void
}>()
</script>

<template>
  <section class="flex overflow-hidden flex-col flex-1 shrink p-2.5 basis-0 min-w-[240px] max-md:max-w-full">
    <header class="flex flex-wrap gap-10 justify-between items-center w-full max-md:max-w-full">
      <h1 class="self-stretch my-auto text-xl font-medium">
        {{ paginatedRoadmaps.total_elements }} Roadmaps
      </h1>
      <Select
        v-if="props.sortBy !== undefined"
        :model-value="sortBy"
        @update:model-value="value => emit('sortChange', value)"
      >
        <SelectTrigger class="w-[180px]">
          <SelectValue placeholder="Ordenar por:"/>
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectLabel>Ordenar</SelectLabel>
            <SelectItem value="recent">Más reciente</SelectItem>
            <SelectItem value="oldest">Más antiguo</SelectItem>
            <SelectItem value="popular">Más popular</SelectItem>
            <SelectItem value="highestRated">Mejor valorado</SelectItem>
          </SelectGroup>
        </SelectContent>
      </Select>
    </header>

    <section class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
      <RoadmapCard
        v-for="roadmap in paginatedRoadmaps.content"
        :key="roadmap.id"
        :roadmap="roadmap"
        @click="roadmap => emit('roadmapClick', roadmap)"
      />
    </section>

    <RoadmapPagination
      v-if="paginatedRoadmaps.total_pages > 0"
      :current-page="paginatedRoadmaps.number + 1"
      :total-pages="paginatedRoadmaps.total_pages"
      :is-loading="isLoading ?? false"
      :on-page-change="page => emit('pageChange', page)"
    />
  </section>
</template>
