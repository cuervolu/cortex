<script setup lang="ts">

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
        <Progress :value="50" class="h-2"/>
        <div class="flex justify-between items-center">
          <span class="text-sm text-gray-500">Completed Courses</span>
          <span class="font-bold">10/23</span>
        </div>
        <Progress :value="43" class="h-2"/>
        <div class="flex justify-between items-center">
          <span class="text-sm text-gray-500">Completed Modules</span>
          <span class="font-bold">29/38</span>
        </div>
        <Progress :value="76" class="h-2"/>
      </CardContent>
    </Card>

    <Card>
      <CardContent class="p-6">
        <div class="relative w-32 h-32 mx-auto">
          <svg class="w-full h-full" viewBox="0 0 36 36">
            <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="#E2E8F0"
                stroke-width="2"
            />
            <path
                d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                fill="none"
                stroke="#4C1D95"
                stroke-width="2"
                stroke-dasharray="70, 100"
            />
          </svg>
          <div
              class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-center">
            <span class="text-3xl font-bold">70%</span>
            <span class="block text-sm text-gray-500">Progress</span>
          </div>
        </div>
      </CardContent>
    </Card>

    <div class="grid grid-cols-2 gap-4">
      <Card>
        <CardContent class="p-6 flex flex-col items-center">
          <div class="bg-pink-100 p-3 rounded-lg mb-2">
            <svg
                xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" class="text-pink-500">
              <rect width="18" height="18" x="3" y="3" rx="2" ry="2"/>
              <path d="M3 9h18"/>
              <path d="M9 21V9"/>
            </svg>
          </div>
          <span class="text-2xl font-bold">42 +</span>
          <span class="text-sm text-gray-500">Exercises</span>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-6 flex flex-col items-center">
          <div class="bg-pink-100 p-3 rounded-lg mb-2">
            <svg
                xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" class="text-pink-500">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" x2="8" y1="13" y2="13"/>
              <line x1="16" x2="8" y1="17" y2="17"/>
              <line x1="10" x2="8" y1="9" y2="9"/>
            </svg>
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