<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { invoke } from '@tauri-apps/api/core'
import type { ExerciseList } from '~/types'

const exercises = ref<ExerciseList[]>([])
const isLoading = ref(true)
const error = ref<string | null>(null)

const fetchExercises = async () => {
  isLoading.value = true
  error.value = null
  try {
    const response = await invoke<ExerciseList[]>('get_exercises')
    console.log('Fetched exercises:', response) // For debugging
    if (Array.isArray(response)) {
      exercises.value = response
    } else {
      throw new Error('Unexpected response format')
    }
    if (exercises.value.length === 0) {
      console.warn('No exercises returned from the API')
    }
  } catch (err) {
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
    <ul v-else class="space-y-4">
      <li v-for="exercise in exercises" :key="exercise.id" class="border p-4 rounded-lg shadow-sm">
        <h2 class="text-xl font-semibold">{{ exercise.title }}</h2>
        <p class="text-gray-600">Points: {{ exercise.points }}</p>
        <NuxtLink :to="`/exercises/${exercise.id}`" class="text-blue-500 hover:underline">
          View Details
        </NuxtLink>
      </li>
    </ul>
    <div class="mt-4">
      <h2 class="text-lg font-semibold">Debug Info:</h2>
      <pre class="bg-gray-100 p-2 rounded mt-2">{{ JSON.stringify(exercises, null, 2) }}</pre>
    </div>
  </div>
</template>