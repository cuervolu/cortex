<script setup lang="ts">

import HomeIcon from "~/components/icons/HomeIcon.vue";
import { useRoadmaps } from '~/composables/useRoadmaps'

import RoadmapHeader from "@cortex/shared/components/roadmaps/RoadmapHeader.vue";
import RoadmapBanner from "@cortex/shared/components/roadmaps/RoadmapBanner.vue";
import RoadmapOverview from "@cortex/shared/components/roadmaps/RoadmapOverview.vue";
import RoadmapInscriptionCard from "@cortex/shared/components/roadmaps/RoadmapInscriptionCard.vue";
import RoadmapSidebar from "@cortex/shared/components/roadmaps/RoadmapSidebar.vue";

import { ref, onMounted } from "vue";

import { useRoute } from "vue-router";

const route = useRoute()
const slug = route.params.slug as string
const { handleError } = useWebErrorHandler()

const { roadmap, fetchRoadmap, loading } = useRoadmaps()

onMounted(async () => {
    try {
        await fetchRoadmap(slug)
    } catch (err) {
        await handleError(err, {
            statusCode: 404,
            data: { slug },
        })
    }
})

const credits = computed(() => {
    if (!roadmap.value) return 0;

    return roadmap.value.courses
        .flatMap(course => course.modules)
        .flatMap(module => module.lessons)
        .reduce((totalCredits, lesson) => totalCredits + lesson.credits, 0);
});
const { $markdown } = useNuxtApp();
</script>

<template>
    <section v-if="loading">Cargando detalles del roadmap...</section>
    
    <section v-else-if="roadmap" class="w-full self-stretch px-10 justify-start items-start gap-[30px] inline-flex">
        <div class="md:w-full lg:w-4/6 flex flex-col gap-7">
            <Breadcrumb class="py-5">
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
                        <BreadcrumbLink>
                            <NuxtLink to="/explore">
                                Explore Roadmaps
                            </NuxtLink>
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbPage>{{ roadmap.title }}</BreadcrumbPage>
                    </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>
            <RoadmapHeader
                :title="roadmap.title"
                :is-published="roadmap.is_published"
                :credits="credits"
            />
            <RoadmapBanner
                :title="roadmap.title"
                :image-url="roadmap.image_url"
            />
            <RoadmapOverview
                :description="roadmap.description"
                :created-at="roadmap.created_at"
                :updated-at="roadmap.updated_at"
                :mentor = "roadmap.mentor"
                :options = "$markdown.options"
            />
            <RoadmapInscriptionCard
                :roadmap="roadmap"
            />
        </div>
        <div class="w-2/6 self-stretch bg-background border-l px-3 min-w-[322px] hidden lg:block">
            <RoadmapSidebar
                :courses="roadmap.courses"
                :tag-names="roadmap.tag_names"
            />
        </div>
    </section>
</template>