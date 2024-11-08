<script setup lang="ts">
import {debug} from "@tauri-apps/plugin-log";
import type {RoadmapCreateRequest} from "@cortex/shared/types";


const handleCreateRoadmap = async (roadmapData: RoadmapCreateRequest) => {
  await debug(`Creating roadmap with data: ${JSON.stringify(roadmapData)}`);
};
</script>

<template>
  <div class="p-8 w-full flex flex-col">
    <div class="mb-6">
      <h1 class="text-2xl font-bold">Crear Nuevo Roadmap</h1>
    </div>

    <RoadmapForm @submit="handleCreateRoadmap"/>
  </div>
</template>