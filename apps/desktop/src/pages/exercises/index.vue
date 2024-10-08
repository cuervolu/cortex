<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { invoke } from '@tauri-apps/api/core'
import type { PaginatedExercises, Exercise } from '@cortex/shared/types'
import {error as logError} from "@tauri-apps/plugin-log";


const exercises = ref<Exercise[]>([])
const isLoading = ref(true)
const error = ref<string | null>(null)
const totalExercises = ref<number>(0)
const currentPage = ref<number>(0)
const totalPages = ref<number>(0)

const fetchExercises = async () => {
  isLoading.value = true
  error.value = null
  try {
    const response = await invoke<PaginatedExercises>('get_exercises')
    console.log('Raw response:', response) // Para depuración
    if (response && Array.isArray(response.content)) {
      exercises.value = response.content
      totalExercises.value = response.total_elements
      currentPage.value = response.number
      totalPages.value = response.total_pages
    } else {
      throw new Error('Unexpected response format')
    }
    if (exercises.value.length === 0) {
      console.warn('No exercises returned from the API')
    }
  } catch (err) {
    await logError(`Failed to fetch exercises: ${err}`)
    console.error('Failed to fetch exercises:', err)
    error.value = err instanceof Error ? err.message : 'An unknown error occurred'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchExercises()
})
</script>

<template>
  <div class="container mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">Exercises</h1>
    <div v-if="isLoading">Loading exercises...</div>
    <div v-else-if="error">Error: {{ error }}</div>
    <div v-else-if="exercises.length === 0">No exercises available.</div>
    <template v-else>
      <ul class="space-y-4">
        <li v-for="exercise in exercises" :key="exercise.id" class="border p-4 rounded-lg shadow-sm">
          <h2 class="text-xl font-semibold">{{ exercise.title }}</h2>
          <p class="text-gray-600">Points: {{ exercise.points }}</p>
          <NuxtLink :to="`/exercises/${exercise.id}`" class="text-blue-500 hover:underline">
            View Details
          </NuxtLink>
        </li>
      </ul>
      <div class="mt-4">
        <p>Page {{ currentPage + 1 }} of {{ totalPages }}</p>
        <p>Total exercises: {{ totalExercises }}</p>
      </div>
    </template>
  </div>
</template>