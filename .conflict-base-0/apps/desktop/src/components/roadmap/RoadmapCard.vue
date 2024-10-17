<script setup lang="ts">
import { Clock, Tag } from 'lucide-vue-next'
import type {Roadmap} from "@cortex/shared/types";

defineProps<{
  roadmap: Roadmap
}>()
</script>

<template>
  <Card class="overflow-hidden">
    <CardHeader class="p-0">
      <img
          v-if="roadmap.image_url"
          :src="roadmap.image_url"
          :alt="roadmap.title"
          class="w-full h-48 object-cover"
      />
      <img
          v-else
          src="https://picsum.photos/200/400"
          alt="Placeholder"
          class="w-full h-48 object-cover bg-gray-200"
      />
    </CardHeader>
    <CardContent class="p-4">
      <CardTitle class="text-xl mb-2">{{ roadmap.title }}</CardTitle>
      <p class="text-muted-foreground mb-4">{{ roadmap.description }}</p>
      <div class="flex flex-wrap gap-2 mb-4">
        <Badge v-for="tag in roadmap.tag_names" :key="tag" variant="secondary" class="flex items-center">
          <Tag class="w-3 h-3 mr-1" />
          {{ tag }}
        </Badge>
      </div>
      <div class="flex items-center justify-between text-sm text-muted-foreground mb-4">
        <span class="flex items-center">
          <Clock class="w-4 h-4 mr-1" />
          {{ new Date(roadmap.created_at).toLocaleDateString() }}
        </span>
        <Badge :variant="roadmap.is_published ? 'default' : 'destructive'">
          {{ roadmap.is_published ? "Published" : "Draft" }}
        </Badge>
      </div>
    </CardContent>
    <CardFooter>
      <Button class="w-full">
        <NuxtLink :to="`/roadmap/${roadmap.slug}`">
          Ver Roadmap
        </NuxtLink>
      </Button>
    </CardFooter>
  </Card>
</template>