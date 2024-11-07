<script setup lang="ts">
import type { Roadmap } from "@cortex/shared/types"

defineProps<{
  roadmap: Roadmap
}>()
</script>

<template>
  <NuxtLink :to="`/explore/${roadmap.slug}`" class="grow shrink basis-0 w-screen min-w-[382px]">
    <Card class="p-[11px] rounded-[18px] flex-col justify-start items-start gap-2.5 inline-flex w-full">
      <CardHeader class="w-full p-2.5">
        <img
          class="rounded-[16px] min-h-[200px] max-h-[200px] object-cover"
          :src="roadmap.image_url || '/api/placeholder/400/200'"
          :alt="roadmap.title"
        >
      </CardHeader>
      <CardContent class="flex gap-2.5 flex-col px-2.5 pb-2.5 w-full">
        <div class="flex items-center gap-1 justify-between">
          <div class="gap-1.5 flex md:max-w-[580px]">
            <Badge
              v-for="tag in roadmap.tag_names.slice(0, 3)"
              :key="tag"
              class="line-clamp-1"
            >
              {{ tag }}
            </Badge>
          </div>
        </div>
        <h2 class="text-xl font-medium line-clamp-1">{{ roadmap.title }}</h2>
        <p class="text-sm text-gray-500 line-clamp-2">{{ roadmap.description }}</p>
      </CardContent>
      <Separator/>
      <CardFooter class="w-full p-2.5">
        <div class="flex justify-between w-full font-medium">
          <div class="flex items-center gap-1">
            <span>{{ roadmap.course_slugs.length }} cursos</span>
          </div>
          <div class="text-sm text-gray-500">
            {{ new Date(roadmap.created_at).toLocaleDateString() }}
          </div>
        </div>
      </CardFooter>
    </Card>
  </NuxtLink>
</template>