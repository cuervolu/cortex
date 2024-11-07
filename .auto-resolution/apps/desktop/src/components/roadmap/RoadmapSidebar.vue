<script setup lang="ts">
import {Tag} from 'lucide-vue-next'
import type {Course} from "@cortex/shared/types";

interface RoadmapSidebarProps {
  courses: Course[]
  tagNames: string[]
}

defineProps<RoadmapSidebarProps>()
</script>

<template>
  <div class="space-y-6">
    <Card>
      <div class="p-4">
        <h2 class="font-semibold">Cursos en este Roadmap</h2>
      </div>
      <ScrollArea class="h-[400px]">
        <div class="p-4">
          <div class="space-y-4">
            <div v-for="course in courses" :key="course.id">
              <div class="flex items-center gap-2">
                <img
                    :src="course.image_url || '/placeholder.svg'"
                    :alt="course.name"
                    class="h-8 w-8 rounded"
                >
                <div>
                  <h3 class="font-medium">{{ course.name }}</h3>
                  <p class="text-sm text-muted-foreground">
                    {{ course.tag_names.join(', ') }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </ScrollArea>
    </Card>

    <div class="space-y-4">
      <h3 class="font-semibold">Topics</h3>
      <div class="flex flex-wrap gap-2">
        <Badge
            v-for="tag in tagNames"
            :key="tag"
            variant="secondary"
            class="flex items-center gap-1"
        >
          <Tag class="h-3 w-3"/>
          {{ tag }}
        </Badge>
      </div>
    </div>
  </div>
</template>