<script setup>
import { ref } from 'vue';
import backgroundImage from '~/assets/img/auth-background.svg'
import TitleBar from '~/components/TitleBar.vue';

const isScrolled = ref(false);

const handleScroll = (event) => {
  isScrolled.value = event.target.scrollTop > 0;
};
</script>

<template>
  <div class="relative min-h-screen flex flex-col">
    <!-- TitleBar -->
    <TitleBar class="sticky top-0 z-50" :has-sidebar="false" :class="{ 'shadow-md': isScrolled }" />

    <!-- Scrollable content -->
    <div class="flex-1 overflow-auto" @scroll="handleScroll">
      <!-- Background image container -->
      <div
          class="fixed inset-0 bg-cover bg-center bg-no-repeat"
          :style="{ backgroundImage: `url(${backgroundImage})` }"
      />

      <!-- Content overlay -->
      <div class="relative w-full flex min-h-full">
        <!-- Left side with text -->
        <div class="flex-1 flex items-center p-8">
          <div>
            <h1 class="text-5xl font-bold text-purple-900 mb-2">
              Start learning <span class="text-pink-500">today</span>.
            </h1>
            <h2 class="text-4xl font-bold text-purple-900">
              Knowledge has <span class="text-teal-400">no limits</span>,
            </h2>
            <h2 class="text-4xl font-bold text-purple-900">
              and neither does your
            </h2>
            <h2 class="text-4xl font-bold text-yellow-400">
              potential
            </h2>
          </div>
        </div>

        <!-- Right side with form -->
        <div class="flex-1 flex justify-center items-center">
          <slot/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.sticky {
  position: sticky;
  top: 0;
}
</style>