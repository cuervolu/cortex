<script setup lang="ts">
import {ChevronDown} from "lucide-vue-next";
</script>

<template>
  <header class="flex items-center justify-between p-4 bg-pink-100 rounded-full mx-4 mt-4">
    <div class="flex items-center">
      <img src="~/assets/img/Cortex%20Logo.svg" alt="Cortex Logo" class="h-10 w-10 mr-2">
      <span class="text-2xl font-bold text-purple-800">Cortex</span>
    </div>
    <nav>
      <ul class="flex space-x-6">
        <li class="flex items-center">
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">Roadmaps</NuxtLink>
          <ChevronDown class="h-4 w-4 ml-1 text-gray-500"/>
        </li>
        <li>
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">About us</NuxtLink>
        </li>
        <li>
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">Mentors</NuxtLink>
        </li>
        <li>
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">Plans</NuxtLink>
        </li>
        <li>
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">Blog</NuxtLink>
        </li>
      </ul>
    </nav>
    <Button variant="default" class="bg-white text-purple-800 hover:bg-gray-100">Get started
    </Button>
  </header>

</template>

<style scoped>

</style>