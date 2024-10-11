<script setup lang="ts">
import {PanelsTopLeft, StickyNote} from 'lucide-vue-next';

const progressData = [
  { name: 'Completed', value: 70 },
  { name: 'Remaining', value: 30 }
];
</script>

<template>
  <div class="space-y-8">
    <Card>
      <CardHeader>
        <CardTitle>User Progress</CardTitle>
      </CardHeader>
      <CardContent class="space-y-4">
        <div class="flex justify-between items-center">
          <span class="text-sm text-gray-500">Completed Roadmaps</span>
          <span class="font-bold">2/4</span>
        </div>
        <Progress :model-value="50" class="h-2"/>
        <div class="flex justify-between items-center">
          <span class="text-sm text-gray-500">Completed Courses</span>
          <span class="font-bold">10/23</span>
        </div>
        <Progress :model-value="43" class="h-2"/>
        <div class="flex justify-between items-center">
          <span class="text-sm text-gray-500">Completed Modules</span>
          <span class="font-bold">29/38</span>
        </div>
        <Progress :model-value="76" class="h-2"/>
      </CardContent>
    </Card>

    <Card>
      <CardContent class="p-6">
        <div class="relative w-32 h-32 mx-auto">
          <h2 class="text-center text-2xl font-bold">Progress</h2>
          <DonutChart
              index="name"
              category="value"
              :data="progressData"
              :colors="['indigo', 'purple']"
              class="w-full h-full"
              
          />
        </div>
      </CardContent>
    </Card>

    <div class="grid grid-cols-2 gap-4">
      <Card>
        <CardContent class="p-6 flex flex-col items-center">
          <div class="bg-pink-100 p-3 rounded-lg mb-2">
            <PanelsTopLeft class="text-pink-500"/>
          </div>
          <span class="text-2xl font-bold">42 +</span>
          <span class="text-sm text-gray-500">Exercises</span>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-6 flex flex-col items-center">
          <div class="bg-pink-100 p-3 rounded-lg mb-2">
            <StickyNote class="text-pink-500"/>
          </div>
          <span class="text-2xl font-bold">20 +</span>
          <span class="text-sm text-gray-500">Lessons</span>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<style scoped>

</style>