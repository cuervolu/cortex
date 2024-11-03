<script setup lang="ts">
import { useRoadmaps } from '~/composables/useRoadmaps'
import RoadmapBreadcrumb from "~/components/roadmap/RoadmapBreadcrumb.vue"
import RoadmapHeader from "~/components/roadmap/RoadmapHeader.vue"
import RoadmapBanner from "~/components/roadmap/RoadmapBanner.vue"
import RoadmapActions from "~/components/roadmap/RoadmapActions.vue"
import RoadmapOverview from "~/components/roadmap/RoadmapOverview.vue"
import RoadmapSidebar from "~/components/roadmap/RoadmapSidebar.vue"

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
</script>

<template>
  <ScrollArea class="flex h-full w-full text-foreground">
    <div v-if="loading">
      <p class="text-gray-500">Loading...</p>
    </div>
    <div v-else-if="roadmap" class="m-[30px] self-stretch">
      <RoadmapBreadcrumb :slug="roadmap.slug" :title="roadmap.title" />
      <div class="container grid grid-cols-1 gap-6 md:grid-cols-3 lg:gap-10">
        <div class="md:col-span-2">
          <RoadmapHeader
              :title="roadmap.title"
              :is-published="roadmap.is_published"
              :tag-names="roadmap.tag_names"
          />
          <RoadmapBanner
              :title="roadmap.title"
              :image-url="roadmap.image_url"
          />
          <RoadmapActions
              :roadmap-id="roadmap.id"
              :is-published="roadmap.is_published"
          />
          <Card class="p-6">
            <RoadmapOverview
                :description="roadmap.description"
                :created-at="roadmap.created_at"
                :updated-at="roadmap.updated_at"
                :courses="roadmap.courses"
            />
          </Card>
        </div>
        <RoadmapSidebar
            :courses="roadmap.courses"
            :tag-names="roadmap.tag_names"
        />
      </div>
    </div>
  </ScrollArea>
</template>