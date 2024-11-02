<script setup lang="ts">
import RoadmapCard from '~/components/explore-roadmaps/RoadmapCard.vue'
import RoadmapCardSkeleton from '~/components/explore-roadmaps/RoadmapCardSkeleton.vue'
import type {PaginatedRoadmaps} from "@cortex/shared/types"
import {useRoadmaps} from "~/composables/useRoadmaps"
import RoadmapPagination from "~/components/explore-roadmaps/RoadmapPagination.vue"

const route = useRoute()
const router = useRouter()
const sortBy = ref(route.query.sort as string || 'recent')
const currentPage = ref(Number(route.query.page) || 1)
const itemsPerPage = 5
const roadmapsData = ref < PaginatedRoadmaps > ({
    content: [],
    total_elements: 0,
    total_pages: 1,
    number: 0,
    size: itemsPerPage,
    first: true,
    last: true
})
const isLoading = ref(true)
const error = ref<string | null>(null)
const {fetchRoadmaps} = useRoadmaps()
const loadRoadmaps = async () => {
  isLoading.value = true
  error.value = null
  try {
    const response = await fetchRoadmaps({
      page: currentPage.value - 1,
      size: itemsPerPage,
      sort: sortBy.value
    })
    if (response.data.value) {
      roadmapsData.value = response.data.value
    }
  } catch (e) {
    error.value = 'Error al cargar los roadmaps'
    console.error('Error loading roadmaps:', e)
  } finally {
    isLoading.value = false
  }
}
onMounted(() => {
  loadRoadmaps()
})
const handleSortChange = async (value: string) => {
  sortBy.value = value
  currentPage.value = 1
  await router.push({
    query: {
      ...route.query,
      sort: value,
      page: '1'
    }
  })
  await loadRoadmaps()
}
watch(
    () => route.query,
    async (newQuery) => {
      const newPage = Number(newQuery.page) || 1
      const newSort = newQuery.sort as string || 'recent'
      if (currentPage.value !== newPage || sortBy.value !== newSort) {
        currentPage.value = newPage
        sortBy.value = newSort
        await loadRoadmaps()
      }
    }
)
</script>
<template>
<<<<<<< ours
  <section
      class="flex overflow-hidden flex-col flex-1 shrink p-2.5 basis-0 min-w-[240px] max-md:max-w-full">
    <header class="flex flex-wrap gap-10 justify-between items-center w-full max-md:max-w-full">
      <h1 class="self-stretch my-auto text-xl font-medium">
        {{ roadmapsData.total_elements }} Roadmaps
      </h1>
      <Select v-model="sortBy" @update:model-value="handleSortChange">
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
    <!-- Loading state -->
    <section
        v-if="isLoading"
        class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
      <RoadmapCardSkeleton v-for="n in 6" :key="n"/>
||||||| ancestor
    <section class="flex overflow-hidden flex-col flex-1 shrink p-2.5 basis-0 min-w-[240px] max-md:max-w-full">
        <header class="flex flex-wrap gap-10 justify-between items-center w-full max-md:max-w-full">
            <h1 class="self-stretch my-auto text-xl font-medium">
                {{ roadmapsData.total_elements }} Roadmaps
            </h1>
            <Select v-model="sortBy" @update:model-value="handleSortChange">
                <SelectTrigger class="w-[180px]">
                    <SelectValue placeholder="Ordenar por:" />
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
        <!-- Loading state -->
        <section v-if="isLoading" class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
            <RoadmapCardSkeleton v-for="n in 6" :key="n" />
        </section>
        <!-- Error state -->
        <div v-else-if="error" class="text-red-500 py-10 text-center">
            {{ error }}
        </div>
        <!-- Content -->
        <template v-else>
            <section v-if="roadmapsData.content.length > 0"
                class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
                <RoadmapCard v-for="roadmap in roadmapsData.content" :key="roadmap.id" :roadmap="roadmap" />
            </section>
            <div v-else class="text-center py-10">
                No se encontraron roadmaps
            </div>
            <RoadmapPagination v-if="roadmapsData.total_pages > 0" :current-page="currentPage"
                :total-pages="roadmapsData.total_pages" :is-loading="isLoading" />
        </template>
=======
    <section class="flex overflow-hidden flex-col flex-1 shrink p-2.5 basis-0 min-w-[240px] max-md:max-w-full">
        <header class="flex flex-wrap gap-10 justify-between items-center w-full max-md:max-w-full">
            <h1 class="self-stretch my-auto text-xl font-medium">
                {{ roadmapsData.total_elements }} Roadmaps
            </h1>
            <Select v-model="sortBy" @update:model-value="handleSortChange">
                <SelectTrigger class="w-[180px]">
                    <SelectValue placeholder="Ordenar por:" />
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
        <!-- Loading state -->
        <section v-if="isLoading" class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
            <RoadmapCardSkeleton v-for="n in 6" :key="n" />
        </section>
        <!-- Error state -->
        <div v-else-if="error" class="text-red-500 py-10 text-center">
            {{ error }}
        </div>
        <!-- Content -->
        <template v-else>
            <section v-if="roadmapsData.content.length > 0"
                class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
                <RoadmapCard v-for="roadmap in roadmapsData.content" :key="roadmap.id" :roadmap="roadmap"/>
            </section>
            <div v-else class="text-center py-10">
                No se encontraron roadmaps
            </div>
            <RoadmapPagination v-if="roadmapsData.total_pages > 0" :current-page="currentPage"
                :total-pages="roadmapsData.total_pages" :is-loading="isLoading" />
        </template>
>>>>>>> theirs
    </section>
    <!-- Error state -->
    <div v-else-if="error" class="text-red-500 py-10 text-center">
      {{ error }}
    </div>
    <!-- Content -->
    <template v-else>
      <section
          v-if="roadmapsData.content.length > 0"
          class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full"
      >
        <RoadmapCard
            v-for="roadmap in roadmapsData.content"
            :key="roadmap.id"
            :roadmap="roadmap"
        />
      </section>
      <div v-else class="text-center py-10">
        No se encontraron roadmaps
      </div>
      <RoadmapPagination
          v-if="roadmapsData.total_pages > 0"
          :current-page="currentPage"
          :total-pages="roadmapsData.total_pages"
          :is-loading="isLoading"
      />
    </template>
  </section>
</template>