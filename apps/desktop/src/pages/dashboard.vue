<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { invoke } from '@tauri-apps/api/core';
import { info, error as logError } from "@tauri-apps/plugin-log";
import type { PaginatedRoadmaps, Roadmap } from "@cortex/shared/types";

const roadmaps = ref<Roadmap[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    const response = await invoke<PaginatedRoadmaps>('fetch_all_roadmaps');
    roadmaps.value = response.content;
    await info(`Fetched ${roadmaps.value.length} roadmaps`);
  } catch (err) {
    await logError(`Failed to fetch roadmaps: ${err instanceof Error ? err.message : 'Unknown error'}`);
    error.value = 'Failed to fetch roadmaps: ' + (err instanceof Error ? err.message : 'Unknown error');
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="h-full overflow-hidden flex flex-col">
    <h1 class="text-3xl font-bold mb-8 text-center">Roadmaps</h1>
    <div v-if="loading" class="text-center">Loading...</div>
    <div v-else-if="error" class="text-red-500 text-center">{{ error }}</div>
    <div v-else class="overflow-y-auto flex-grow">
      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6 p-4">
        <NuxtLink
            v-for="roadmap in roadmaps"
            :key="roadmap.id"
            :to="{ name: 'roadmap-slug', params: { slug: roadmap.slug } }"
            class="flex"
        >
          <Card class="w-full flex flex-col">
            <CardHeader class="p-0">
              <img
                  v-if="roadmap.image_url"
                  :src="roadmap.image_url"
                  :alt="roadmap.title"
                  class="w-full h-48 object-cover rounded-t-lg"
              >
              <div
                  v-else
                  class="w-full h-48 bg-gray-200 flex items-center justify-center rounded-t-lg"
              >
                <span class="text-gray-500">No image available</span>
              </div>
            </CardHeader>
            <CardContent class="p-6 flex-grow">
              <h2 class="text-xl font-semibold mb-2">{{ roadmap.title }}</h2>
              <p class="text-gray-600 mb-4 line-clamp-3">{{ roadmap.description }}</p>
              <div class="mb-4 flex flex-wrap">
                <span
                    v-for="tag in roadmap.tag_names"
                    :key="tag"
                    class="inline-block bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full mr-2 mb-2"
                >
                  {{ tag }}
                </span>
              </div>
              <div class="mb-4">
                <h3 class="text-sm font-semibold mb-1">Associated Courses:</h3>
                <ul class="list-disc list-inside">
                  <li
                      v-for="course in roadmap.course_slugs"
                      :key="course"
                      class="text-sm text-gray-600 truncate"
                  >
                    {{ course }}
                  </li>
                </ul>
              </div>
            </CardContent>
            <CardFooter class="text-sm text-gray-500 mt-auto">
              <p>Created: {{ new Date(roadmap.created_at).toLocaleDateString() }}</p>
              <p v-if="roadmap.updated_at">
                Updated: {{ new Date(roadmap.updated_at).toLocaleDateString() }}
              </p>
            </CardFooter>
          </Card>
        </NuxtLink>
      </div>
    </div>
  </div>
</template>