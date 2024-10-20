<script setup lang="ts">

import GithubIcon from "~/components/icons/GithubIcon.vue";
import TwitterIcon from "~/components/icons/TwitterIcon.vue";
import InstagramIcon from "~/components/icons/InstagramIcon.vue";
</script>

<template>
  <div class="flex gap-3 justify-center items-center">
    <a href="#" aria-label="Github">
      <github-icon class="object-contain shrink-0 self-stretch my-auto aspect-square w-[41px]" />
    </a>
    <a href="#" aria-label="Twitter">
      <twitter-icon class="object-contain shrink-0 self-stretch my-auto aspect-square w-[41px]" />
    </a>
    <a href="#" aria-label="Instagram">
      <instagram-icon class="object-contain shrink-0 self-stretch my-auto aspect-square w-[41px]"  />
    </a>
  </div>
</template>


<style scoped>

</style>