<script setup lang="ts">
import type { Roadmap } from '@cortex/shared/types'
import FilterSiderbar from "~/components/explore-roadmaps/FilterSidebar.vue";
import RoadmapList from '@cortex/shared/components/roadmaps/RoadmapList.vue'
import HomeIcon from "~/components/icons/HomeIcon.vue";
import { useRoadmaps } from "~/composables/useRoadmaps";

const router = useRouter()
const {data} = useAuth();
const { paginatedRoadmaps, loading, fetchRoadmaps } = useRoadmaps()
const sortBy = ref('recent')

const handlePageChange = (page: number) => {
  fetchRoadmaps({ page: page - 1 })
}

const getSortParam = (sort: string): string[] => {
  switch (sort) {
    case 'recent':
      return ['createdAt:desc']
    case 'oldest':
      return ['createdAt:asc']
    case 'title':
      return ['title:asc']
    default:
      return ['createdAt:desc']
  }
}

const handleSortChange = (sort: string) => {
  sortBy.value = sort

  if (data?.value?.roles.includes('ADMIN')) {
    fetchRoadmaps({isAdmin: true, sort: getSortParam(sort)})
  }
  fetchRoadmaps({sort: getSortParam(sort)})
}

const handleRoadmapClick = (roadmap: Roadmap) => {
  router.push(`/explore/${roadmap.slug}`)
}

onMounted(() => {
  if (data?.value?.roles.includes('ADMIN')) {
    fetchRoadmaps({isAdmin: true})
  }

  fetchRoadmaps()
})

</script>


<template>
  <section>
    <div class="w-full flex-col justify-start items-start gap-[25px] inline-flex">
      <Breadcrumb class="px-10 py-5">
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink>
              <NuxtLink to="/">
                <HomeIcon class="w-[18px] fill-current"/>
              </NuxtLink>
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbLink>
              <NuxtLink to="/roadmaps">
                Roadmaps
              </NuxtLink>
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbPage>Explore Roadmaps</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>

      <div class="self-stretch w-full px-[38px] py-[30px] flex-col justify-center md:items-center items-start gap-[22px] flex">
        <div class="text-primary dark:text-[#E1E0E0] text-5xl font-semibold">
          Explore Roadmaps
        </div>
        <div>
          <div class="self-stretch md:text-center max-w-[900px] text-left text-neutral-700 dark:text-[#BEBEBE] text-xl font-normal">
            Descubre nuestros Roadmaps interactivos y empieza tu viaje de aprendizaje paso a paso.
            Desde los fundamentos hasta niveles avanzados, te guiamos por el camino hacia el dominio
            de las tecnologías más demandadas
          </div>
        </div>
      </div>

      <div class="self-stretch p-5 rounded-3xl mx-10 bg-[#f8efff] dark:bg-[#361C4A] flex-col justify-start items-center gap-[30px] flex">
        <div class="self-stretch py-[19px] justify-start items-start gap-2.5 inline-flex">
          <FilterSiderbar class="hidden lg:flex"/>
          <ClientOnly>
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
              @roadmap-click="handleRoadmapClick"/>
          </ClientOnly>
        </div>
      </div>
    </div>
  </section>
</template>
