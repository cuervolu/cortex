<script setup lang="ts">
import { Clock } from 'lucide-vue-next'
import type {Course} from "@cortex/shared/types";

export interface RoadmapOverviewProps {
  description: string
  createdAt: string
  updatedAt: string | null
  courses: Course[]
}

defineProps<RoadmapOverviewProps>()

const formatDate = (date: string) => new Date(date).toLocaleDateString()
</script>

<template>
  <div class="grid gap-6">
    <div class="flex items-center gap-4 text-sm text-muted-foreground">
      <div class="flex items-center gap-2">
        <Clock class="h-4 w-4" />
        <span>Creado el {{ formatDate(createdAt) }}</span>
      </div>
      <div v-if="updatedAt" class="flex items-center gap-2">
        <Clock class="h-4 w-4" />
        <span>Actualizado el {{ formatDate(updatedAt) }}</span>
      </div>
    </div>

    <div class="grid gap-4">
      <h3 class="font-semibold">Descripción</h3>
      <MDC class="prose max-w-none text-muted-foreground" :value="description" />
    </div>

    <div class="grid gap-4">
      <h3 class="font-semibold">Cursos Incluidos ({{ courses.length }})</h3>
      <div class="grid gap-4 sm:grid-cols-2">
        <div
            v-for="course in courses"
            :key="course.id"
            class="flex items-center gap-2"
        >
          <img
              :src="course.image_url || '/placeholder.svg'"
              :alt="course.name"
              class="h-8 w-8 rounded"
          >
          <span>{{ course.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>