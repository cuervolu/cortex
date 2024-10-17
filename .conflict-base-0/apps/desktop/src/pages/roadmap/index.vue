<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { invoke } from '@tauri-apps/api/core';
import { info, error as logError } from "@tauri-apps/plugin-log";
import type { PaginatedRoadmaps } from "@cortex/shared/types";
import RoadmapList from "~/components/roadmap/RoadmapList.vue";

const paginatedRoadmaps = ref<PaginatedRoadmaps | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const fetchRoadmaps = async (page: number = 0) => {
  try {
    loading.value = true;
    error.value = null;
    const response = await invoke<PaginatedRoadmaps>('fetch_all_roadmaps', { page, size: 10 });
    paginatedRoadmaps.value = response;
    await info(`Fetched ${response.content.length} roadmaps`);
  } catch (err) {
    await logError(`Failed to fetch roadmaps: ${err instanceof Error ? err.message : 'Unknown error'}`);
    error.value = 'Failed to fetch roadmaps: ' + (err instanceof Error ? err.message : 'Unknown error');
  } finally {
    loading.value = false;
  }
};

const onPageChange = (page: number) => {
  fetchRoadmaps(page - 1);
};

onMounted(() => fetchRoadmaps());
</script>

<template>
  <div class="self-stretch m-[30px]">
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="i in 6" :key="i" class="space-y-3">
        <Skeleton class="h-48 w-full" />
        <Skeleton class="h-4 w-3/4" />
        <Skeleton class="h-4 w-1/2" />
        <div class="flex space-x-2">
          <Skeleton class="h-6 w-16" />
          <Skeleton class="h-6 w-16" />
        </div>
      </div>
    </div>

    <div v-else-if="error" class="text-center text-xl mt-8 text-red-500">
      {{ error }}
    </div>

    <RoadmapList
        v-else-if="paginatedRoadmaps"
        :paginated-roadmaps="paginatedRoadmaps"
        @page-change="onPageChange"
    />
  </div>
</template>