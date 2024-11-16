<script setup lang="ts">
import type { PaginatedRoadmaps, Roadmap } from "@cortex/shared/types"
import RoadmapPagination from "@cortex/shared/components/roadmaps/RoadmapPagination.vue";
import RoadmapCardSkeleton from "@cortex/shared/components/roadmaps/RoadmapCardSkeleton.vue";
import RoadmapCard from "@cortex/shared/components/roadmaps/RoadmapCard.vue";

const props = defineProps<{
  paginatedRoadmaps: PaginatedRoadmaps
  isLoading?: boolean
  sortBy?: string,
  enrolledOnly?: boolean
  enrollmentsCount?: number
}>()

const emit = defineEmits<{
  (e: 'sortChange', value: string): void
  (e: 'pageChange', page: number): void
  (e: 'roadmapClick', roadmap: Roadmap): void
}>()

const sortOptions = [
  { value: 'recent', label: 'Más reciente' },
  { value: 'oldest', label: 'Más antiguo' },
  { value: 'title', label: 'Nombre' },
]
</script>

<template>
  <section class="flex overflow-hidden flex-col flex-1 shrink p-2.5 basis-0 min-w-[240px] max-md:max-w-full">
    <header class="flex flex-wrap gap-10 justify-between items-center w-full max-md:max-w-full">
      <h1 v-if="enrolledOnly" class="self-stretch my-auto text-xl font-medium">
        {{ enrollmentsCount }} Roadmaps
      </h1>
      <h1 v-else class="self-stretch my-auto text-xl font-medium">
        {{ paginatedRoadmaps.total_elements }} Roadmaps
      </h1>
      <Select
        v-if="sortBy"
        :model-value="sortBy"
        @update:model-value="(value: string) => emit('sortChange', value)"
      >
        <SelectTrigger class="w-[180px]">
          <SelectValue placeholder="Ordenar por:"/>
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectLabel>Ordenar</SelectLabel>
            <SelectItem
              v-for="option in sortOptions"
              :key="option.value"
              :value="option.value"
            >
              {{ option.label }}
            </SelectItem>
          </SelectGroup>
        </SelectContent>
      </Select>
    </header>

    <section
        v-if="isLoading"
        class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
      <RoadmapCardSkeleton v-for="n in 6" :key="n"/>
    </section>

    <section v-else class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
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
