<script setup lang="ts">
import { useRoadmaps } from '~/composables/useRoadmaps'
import RoadmapBreadcrumb from "~/components/roadmap/RoadmapBreadcrumb.vue"
import RoadmapHeader from '@cortex/shared/components/roadmaps/RoadmapHeader.vue'
import RoadmapBanner from '@cortex/shared/components/roadmaps/RoadmapBanner.vue'
import RoadmapOverview from '@cortex/shared/components/roadmaps/RoadmapOverview.vue'
import RoadmapSidebar from '@cortex/shared/components/roadmaps/RoadmapSidebar.vue'
import RoadmapInscriptionCard from '~/components/roadmap/RoadmapInscriptionCard.vue'

const route = useRoute()
const slug = route.params.slug as string
const { roadmap, loading, fetchRoadmap } = useRoadmaps()
const { handleError } = useDesktopErrorHandler()

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

</script>

<template>
  <ScrollArea class="flex h-full w-full py-4 text-foreground">
    <section v-if="loading">
      <p class="text-gray-500">Loading...</p>
    </section>
    <section v-else-if="roadmap" class="w-full self-stretch px-10 justify-start items-start gap-[30px] inline-flex">
      <div class="md:w-full lg:w-4/6 flex flex-col gap-7">
        <RoadmapBreadcrumb :slug="roadmap.slug" :title="roadmap.title" />
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
          :mentor="roadmap.mentor"
        />
        <RoadmapInscriptionCard
          :roadmap="roadmap"/>
      </div>
      <div class="w-2/6 self-stretch bg-background border-l px-3 min-w-[322px] hidden lg:block">
        <RoadmapSidebar
        :courses="roadmap.courses"
        :tag-names="roadmap.tag_names"
        />
      </div>
    </section>
  </ScrollArea>
</template>