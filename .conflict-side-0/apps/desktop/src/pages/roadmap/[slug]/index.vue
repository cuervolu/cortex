<script setup lang="ts">
import {invoke} from "@tauri-apps/api/core";
import type {RoadmapDetails} from "@cortex/shared/types";
import {error as logError, info} from "@tauri-apps/plugin-log";
import {onMounted, ref} from "vue";
import {Clock, Tag, Book} from 'lucide-vue-next'

const route = useRoute();
const router = useRouter();
const slug = ref(route.params.slug);
const roadmap = ref<RoadmapDetails | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    const response = await invoke<RoadmapDetails>('get_roadmap', {slug: slug.value});
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
    <div v-else-if="roadmap" class="self-stretch m-[30px]">
      <Card class="mb-8 bg-muted/40 rounded-lg shadow-lg overflow-hidden">
        <CardHeader class="relative">
          <img
              v-if="roadmap.image_url"
              :src="roadmap.image_url"
              :alt="roadmap.title"
              width="1200"
              height="400"
              class="w-full h-[400px] object-cover rounded-t-lg"
          >
          <img
              v-else
              src="https://picsum.photos/400/1200"
              alt="Placeholder"
              width="1200"
              height="400"
              class="w-full h-[400px] object-cover rounded-t-lg bg-gray-200"
          >
          <div class="absolute bottom-4 left-4 right-4 bg-black bg-opacity-60 p-4 rounded">
            <CardTitle class="text-3xl font-bold text-white mb-2">{{ roadmap.title }}</CardTitle>
            <div class="flex flex-wrap gap-2">
              <Badge
                  v-for="tag in roadmap.tag_names" :key="tag" variant="secondary"
                  class="flex items-center">
                <Tag class="w-3 h-3 mr-1"/>
                {{ tag }}
              </Badge>
            </div>
          </div>
        </CardHeader>
        <CardContent class="p-6">
          <div class="flex items-center justify-between text-sm text-muted-foreground mb-4">
            <span class="flex items-center">
              <Clock class="w-4 h-4 mr-1"/>
              Creado el {{ new Date(roadmap.created_at).toLocaleDateString() }}
            </span>
            <Badge :variant="roadmap.is_published ? 'default' : 'destructive'">
              {{ roadmap.is_published ? "Publicado" : "Borrador" }}
            </Badge>
          </div>
          <Separator class="my-4"/>
          <div class="prose max-w-none">
            <MDC :value="roadmap.description" tag="article"/>
          </div>
        </CardContent>
      </Card>

      <h2 class="text-2xl font-bold mb-4">Cursos en este Roadmap</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <Card v-for="course in roadmap.courses" :key="course.id" class="flex flex-col">
          <CardHeader class="p-0">
            <img
                v-if="course.image_url"
                :src="course.image_url"
                :alt="course.name"
                width="400"
                height="200"
                class="w-full h-48 object-cover rounded-t-lg"
            >
            <img
                v-else
                src="https://picsum.photos/200/400"
                alt="Placeholder"
                width="400"
                height="200"
                class="w-full h-48 object-cover rounded-t-lg bg-gray-200"
            >
          </CardHeader>
          <CardContent class="p-4 flex-grow">
            <CardTitle class="text-xl mb-2">{{ course.name }}</CardTitle>
            <p class="text-muted-foreground mb-4 line-clamp-3">{{ course.description }}</p>
            <div class="flex flex-wrap gap-2 mb-4">
              <Badge
                  v-for="tag in course.tag_names" :key="tag" variant="outline"
                  class="flex items-center">
                <Tag class="w-3 h-3 mr-1"/>
                {{ tag }}
              </Badge>
            </div>
          </CardContent>
          <CardFooter class="p-4 mt-auto">
            <p class="text-sm mb-2">Created: {{
                new Date(course.created_at).toLocaleDateString()
              }}</p>
            <p v-if="course.updated_at" class="text-sm mb-2">
              Updated: {{ new Date(course.updated_at).toLocaleDateString() }}
            </p>
            <NuxtLink class="w-full" :to="{ name: 'roadmap-slug-courses-slug', params: { slug: course.slug } }">
              <Button class="w-full">
                <Book class="w-4 h-4 mr-2"/>
                Ver Curso
              </Button>
            </NuxtLink>
          </CardFooter>
        </Card>
      </div>
    </div>
    <div v-else>
      <p class="text-gray-500">Loading...</p>
    </div>
  </ScrollArea>
</template>