<script setup>
import backgroundImage from '~/assets/img/auth-background.svg'
</script>

<template>
  <div class="relative min-h-screen flex">
    <!-- Background image container -->
    <div
        class="absolute inset-0 bg-cover bg-center bg-no-repeat"
        :style="{ backgroundImage: `url(${backgroundImage})` }"
    />

    <!-- Content overlay -->
    <div class="relative w-full flex">
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
</template>
