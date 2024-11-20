<script setup lang="ts">
import type { Roadmap } from '@cortex/shared/types'
import RoadmapList from '@cortex/shared/components/roadmaps/RoadmapList.vue'
import RoadmapCardSkeleton from '@cortex/shared/components/roadmaps/RoadmapCardSkeleton.vue'
import { useRoadmaps } from '~/composables/useRoadmaps'
import HomeIcon from "@cortex/shared/components/icons/HomeIcon.vue"; ;

const router = useRouter()
const { data } = useAuth();
const { paginatedRoadmaps, loading, fetchRoadmaps, enrollments } = useRoadmaps()
const sortBy = ref<string>('recent')

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
        fetchRoadmaps({ isAdmin: true, sort: getSortParam(sort) })
    }
    fetchRoadmaps({ sort: getSortParam(sort) })
}

const handleRoadmapClick = (roadmap: Roadmap) => {
    router.push(`/my-roadmaps/${roadmap.slug}`)
}

onMounted(() => {
    if (data?.value?.roles.includes('ADMIN')) {
        fetchRoadmaps({ isAdmin: true, enrolledOnly: true })
    }

    fetchRoadmaps({ enrolledOnly: true })
})

// enrolled count
const enrollmentsCount = computed(() => {
    return enrollments.value?.length
})

</script>

<template>
    <div class="self-stretch px-10 py-5 space-y-4">
        <Breadcrumb class="px-2 py-5">
            <BreadcrumbList>
                <BreadcrumbItem>
                    <BreadcrumbLink>
                        <NuxtLink to="/">
                            <HomeIcon class="w-[18px] fill-current" />
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
                    <BreadcrumbPage>Mis Roadmaps</BreadcrumbPage>
                </BreadcrumbItem>
            </BreadcrumbList>
        </Breadcrumb>

        <div
            class="self-stretch w-full px-[38px] py-[30px] flex-col justify-center lg:items-center items-start gap-[22px] flex">
            <div class="text-primary dark:text-[#E1E0E0] text-5xl font-semibold">
                Mis Roadmaps
            </div>
            <div>
                <div class="self-stretch lg:text-center max-w-[900px] text-left text-neutral-700 dark:text-[#BEBEBE] text-xl font-normal">
                    Aquí encontrarás todos tus roadmaps guardados y podrás hacer seguimiento de tu progreso
                    en cada uno de ellos. Continúa tu viaje de aprendizaje y mantén el control de tu
                    desarrollo profesional.
                </div>
            </div>
        </div>

        <section v-if="loading" class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
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
            :enrolled-only="true"
            :enrollments-count="enrollmentsCount"
        />
    </div>
</template>