<script setup lang="ts">

import GithubIcon from "~/components/icons/GithubIcon.vue";
import TwitterIcon from "~/components/icons/TwitterIcon.vue";
import InstagramIcon from "~/components/icons/InstagramIcon.vue";
</script>

<template>
  <div class="justify-center items-center gap-[11px] inline-flex">
    <a href="#" aria-label="Github"
      class="w-[41px] h-[41px] rounded-full border border-[#9cacaf] flex justify-center items-center transition-all hover:border-[#97c8d1] hover:bg-[#b3e1e9]">
      <github-icon class="w-[22px] transition-transform hover:scale-110 hover:text-white" />
    </a>
    <!-- Twitter Icon -->
    <a href="#" aria-label="Twitter"
      class="w-[41px] h-[41px] rounded-full border border-[#9cacaf] flex justify-center items-center transition-all hover:border-[#97c8d1] hover:bg-[#b3e1e9]">
      <twitter-icon class="w-[22px] transition-transform hover:scale-110 hover:text-white" />
    </a>

    <!-- Instagram Icon -->
    <a href="#" aria-label="Instagram"
      class="w-[41px] h-[41px] rounded-full border border-[#9cacaf] flex justify-center items-center transition-all hover:border-[#97c8d1] hover:bg-[#b3e1e9]">
      <instagram-icon class="w-[22px] transition-transform hover:scale-110 hover:text-white" />
    </a>
  </div>
</template>


<style scoped></style>