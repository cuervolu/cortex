<script setup lang="ts">
import { invoke } from '@tauri-apps/api/core'
import type { ExerciseDetail } from '~/types'
import { info, error as logError } from '@tauri-apps/plugin-log';

const route = useRoute()
const exercise = ref<ExerciseDetail | null>(null)
const isLoading = ref(true)
const error = ref<string | null>(null)

const fetchExerciseDetails = async () => {
  const id = Number(route.params.id)
  await info(`Fetching exercise details for ID: ${id}`)
  isLoading.value = true
  error.value = null
  try {
    exercise.value = await invoke<ExerciseDetail>('get_exercise_details', { id })
    await info(`Successfully fetched exercise details for ID: ${id}`)
  } catch (err) {
    await logError(`Failed to fetch exercise details: ${err}`)
    console.error('Failed to fetch exercise details:', err)
    error.value = err instanceof Error ? err.message : 'An unknown error occurred'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchExerciseDetails()
})
</script>

<template>
  <div class="container mx-auto p-4">
    <div v-if="isLoading">Loading exercise details...</div>
    <div v-else-if="error">Error: {{ error }}</div>
    <div v-else-if="exercise">
      <h1 class="text-2xl font-bold mb-4">{{ exercise.title }}</h1>
      <div class="mb-4">
        <h2 class="text-xl font-semibold">Instructions</h2>
        <p>{{ exercise.instructions }}</p>
      </div>
      <div class="mb-4">
        <h2 class="text-xl font-semibold">Hints</h2>
        <p>{{ exercise.hints }}</p>
      </div>
      <NuxtLink :to="`/exercises/${exercise.id}/solve`" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
        Start Exercise
      </NuxtLink>
    </div>
    <div v-else>No exercise details available.</div>
  </div>
</template>