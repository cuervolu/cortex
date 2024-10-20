<!-- UserRoadmaps.vue -->
<script setup lang="ts">
import { ref } from 'vue';
import { ChevronRight, Coffee, Languages, Monitor } from 'lucide-vue-next';

const roadmaps = ref([
  {
    title: 'Introduction to the programming world',
    icon: Monitor,
    badges: ['Introduction', 'Software', 'Algoritms', 'Python'],
  },
  {
    title: 'Compiled coffee',
    icon: Coffee,
    badges: ['Introduction', 'Software', 'Algoritms', 'Java'],
  },
  {
    title: 'Code Alchemy',
    icon: Languages,
    badges: ['Advanced', 'Compilers', 'Algoritms', 'C', 'Rust'],
  },
]);
</script>

<template>
  <div class="space-y-8">
    <h2 class="text-2xl font-bold">My Roadmaps</h2>
    <Card v-for="roadmap in roadmaps" :key="roadmap.title">
      <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle class="text-sm font-medium">
          {{ roadmap.title }}
        </CardTitle>
        <component :is="roadmap.icon" class="h-4 w-4 text-muted-foreground"/>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-2">
          <Badge v-for="badge in roadmap.badges" :key="badge" variant="secondary">{{ badge }}</Badge>
        </div>
      </CardContent>
    </Card>
    <Button class="w-full" as-child>
      <NuxtLink to="/roadmap">
      View all roadmaps
      </NuxtLink>
      <ChevronRight class="ml-2 h-4 w-4"/>
    </Button>
  </div>
</template>