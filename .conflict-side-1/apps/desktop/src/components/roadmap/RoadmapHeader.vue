<script setup lang="ts">
import { CheckCircle2, Tag } from 'lucide-vue-next'

interface RoadmapHeaderProps {
  title: string
  isPublished: boolean
  tagNames: string[]
}

defineProps<RoadmapHeaderProps>()
</script>

<template>
  <div class="mb-6">
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-2">
        <h1 class="text-2xl font-bold">{{ title }}</h1>
        <CheckCircle2 v-if="isPublished" class="h-5 w-5 text-green-500" />
      </div>
    </div>

    <div class="mt-4 flex flex-wrap gap-2">
      <Badge
          v-for="tag in tagNames"
          :key="tag"
          variant="secondary"
          class="flex items-center gap-1"
      >
        <Tag class="h-3 w-3" />
        {{ tag }}
      </Badge>
    </div>
  </div>
</template>