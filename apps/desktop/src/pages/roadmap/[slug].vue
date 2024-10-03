<script setup lang="ts">
import {invoke} from "@tauri-apps/api/core";
import type {RoadmapDetail} from "~/types";
import {error as logError, info} from "@tauri-apps/plugin-log";
import {onMounted, ref} from "vue";

const route = useRoute();
const router = useRouter();
const slug = ref(route.params.slug);
const roadmap = ref<RoadmapDetail | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    const response = await invoke<RoadmapDetail>('get_roadmap', {slug: slug.value});
    console.log(response);

    if (!response) {
      await router.push({name: '404'});
      return;
    }

    roadmap.value = response;
    await info(`Successfully fetched roadmap: ${roadmap.value.title}`);
  } catch (err) {
    await logError(`Failed to fetch roadmap: ${err instanceof Error ? err.message : 'Unknown error'}`);
    error.value = 'Failed to fetch roadmap: ' + (err instanceof Error ? err.message : 'Unknown error');

    await router.push({name: '404'});
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <ScrollArea v-if="!loading" class="flex h-full w-full text-foreground">
    <div v-if="error" class="text-red-500">{{ error }}</div>
    <div v-else-if="roadmap">
      <Card class="bg-muted/40 rounded-lg shadow-lg overflow-hidden">
        <!-- Roadmap Header -->
        <CardHeader class="relative">
          <img
              v-if="roadmap.image_url" :src="roadmap.image_url" :alt="roadmap.title"
              class="w-full h-64 object-cover">
          <div v-else class="w-full h-64 bg-gray-200 flex items-center justify-center">
            <span class="text-gray-500">No image available</span>
          </div>
          <CardTitle
              class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black to-transparent p-6">
            <h1 class="text-3xl font-bold text-foreground mb-2">{{ roadmap.title }}</h1>
            <CardDescription class="flex flex-wrap gap-2">
              <Badge
                  v-for="tag in roadmap.tag_names" :key="tag">
                {{ tag }}
              </Badge>
            </CardDescription>
          </CardTitle>
        </CardHeader>

        <!-- Roadmap Details -->
        <CardContent class="p-6">
          <p class="mb-4">{{ roadmap.description }}</p>
          <div class="text-sm mb-4">
            <p>Created: {{ new Date(roadmap.created_at).toLocaleDateString() }}</p>
            <p v-if="roadmap.updated_at">Updated: {{
                new Date(roadmap.updated_at).toLocaleDateString()
              }}</p>
          </div>

          <!-- Associated Courses -->
          <h2 class="text-2xl font-semibold mb-4">Associated Courses</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 ">
            <Card
                v-for="course in roadmap.courses" :key="course.id">
              <CardHeader>
                <img
                    v-if="course.image_url" :src="course.image_url" :alt="course.name"
                    class="w-full h-40 object-cover rounded-t-lg mb-4">
                <div
                    v-else
                    class="w-full h-40 bg-gray-200 flex items-center justify-center rounded-t-lg mb-4">
                  <span class="text-gray-500">No image available</span>
                </div>
                <CardTitle class="text-xl font-semibold mb-2">{{ course.name }}</CardTitle>
                <CardDescription class="mb-3">{{ course.description }}</CardDescription>
              </CardHeader>
              <CardContent class="flex flex-wrap gap-2 mb-3">
                <span
                    v-for="tag in course.tag_names" :key="tag"
                    class="bg-green-100 text-green-800 text-xs px-2 py-1 rounded-full">
                  {{ tag }}
                </span>
              </CardContent>
              <CardFooter class="text-sm ">
                <p>Modules: {{ course.module_ids?.length }}</p>
                <p>Created: {{ new Date(course.created_at).toLocaleDateString() }}</p>
                <p v-if="course.updated_at">Updated:
                  {{ new Date(course.updated_at).toLocaleDateString() }}</p>
              </CardFooter>
            </Card>
          </div>
        </CardContent>
      </Card>
    </div>
    <div v-else>
      <p class="text-gray-500">Loading...</p>
    </div>
  </ScrollArea>
</template>