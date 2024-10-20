<script setup lang="ts">
import {ChevronDown} from "lucide-vue-next";
</script>

<template>
  <div class="w-screen px-10 py-5 flex-col justify-start items-center gap-2.5 inline-flex">
    <header class="self-stretch px-[39px] py-5 bg-[#fae7ef] rounded-[30px] justify-between items-center inline-flex">
    <div class="w-[127px] h-[36.89px] justify-between items-center inline-flex">
      <img src="~/assets/img/Cortex%20Logo.svg" alt="Cortex Logo" class="w-8 h-[36.89px]">
      <span class="w-[90px] h-[25px] font-bold text-[#381653] leading-7 tracking-[4px]">CORTEX</span>
    </div>
    <nav>
      <ul class="flex gap-6">
        <li class="px-2.5 py-1.5 justify-center items-center gap-2.5 flex">
          <NuxtLink to="/explore-roadmaps" class="text-gray-700 hover:text-gray-900">Roadmaps</NuxtLink>
          <ChevronDown class="h-4 w-4 ml-1 text-gray-500"/>
        </li>
        <li class="px-2.5 py-1.5 justify-center items-center gap-2.5 flex">
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">About us</NuxtLink>
        </li>
        <li class="px-2.5 py-1.5 justify-center items-center gap-2.5 flex">
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">Mentors</NuxtLink>
        </li>
        <li class="px-2.5 py-1.5 justify-center items-center gap-2.5 flex">
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">Plans</NuxtLink>
        </li>
        <li class="px-2.5 py-1.5 justify-center items-center gap-2.5 flex">
          <NuxtLink to="#" class="text-gray-700 hover:text-gray-900">Blog</NuxtLink>
        </li>
      </ul>
    </nav>
    <Button variant="default" class="bg-white text-center text-sm font-bold leading-7 rounded-[73px] px-8 py-3 h-[52px] text-[#381653] hover:bg-gray-100">Get started
    </Button>
  </header>
  </div>

</template>

<style scoped>

</style>