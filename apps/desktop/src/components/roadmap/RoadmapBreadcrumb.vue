<script setup lang="ts">
import { Home, ChevronRight } from 'lucide-vue-next'

interface RoadmapBreadcrumbProps {
  slug: string
  title: string
}

defineProps<RoadmapBreadcrumbProps>()
</script>

<template>
  <div class="flex items-center gap-2 p-4 text-sm text-muted-foreground">
    <Home class="h-4 w-4" />
    <NuxtLink to="/roadmaps">Roadmaps</NuxtLink>
    <ChevronRight class="h-4 w-4" />
    <NuxtLink to="/roadmaps">Explore Roadmaps</NuxtLink>
    <ChevronRight class="h-4 w-4" />
    <span>{{ title }}</span>
  </div>
</template>