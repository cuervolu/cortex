<script setup>
import {ref, computed} from 'vue'
import {CodeIcon, UsersIcon, RocketIcon} from 'lucide-vue-next'

const currentYear = computed(() => new Date().getFullYear())

const features = ref([
  {
    name: 'Interactive Lessons',
    description: 'Engage with hands-on coding exercises and projects that make learning computer science fun and exciting.',
    icon: CodeIcon,
  },
  {
    name: 'Community Learning',
    description: 'Connect with other young coders, share your projects, and learn together in a supportive environment.',
    icon: UsersIcon,
  },
  {
    name: 'Progress Tracking',
    description: 'Track your learning journey, earn badges, and celebrate your achievements as you grow your skills.',
    icon: RocketIcon,
  },
])
</script>


<template>
  <div class="min-h-screen bg-muted/60 flex flex-col">
    <header class="bg-muted/90 shadow-sm">
      <div class="max-w-7xl mx-auto px-4 py-4 sm:px-6 lg:px-8 flex justify-between items-center">
        <div class="flex items-center">
          <img src="~/assets/img/icon2_dark.svg" class="w-8 h-8" alt="Cortex's Logo">
          <span class="ml-2 text-2xl font-bold text-foreground">Cortex</span>
        </div>
        <nav>
          <Button as-child variant="ghost">
            <NuxtLink to="#">
              About
            </NuxtLink>
          </Button>
          <Button as-child variant="ghost">
            <NuxtLink to="#">
              Features
            </NuxtLink>
          </Button>

          <Button as-child variant="ghost">
            <NuxtLink to="#">
              Community
            </NuxtLink>
          </Button>
        </nav>
      </div>
    </header>

    <main class="flex-grow">
      <div class="w-full mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div class="text-center">
          <h1 class="text-4xl tracking-tight font-extrabold text-foreground sm:text-5xl md:text-6xl">
            <span class="block">Welcome to Cortex</span>
            <span class="block text-indigo-600">Learn Computer Science, Have Fun!</span>
          </h1>
          <p class="mt-3 max-w-md mx-auto text-base text-gray-500 sm:text-lg md:mt-5 md:text-xl md:max-w-3xl">
            Cortex is a free, open-source educational platform designed to teach computer science to
            kids and teenagers in an engaging and interactive way.
          </p>
          <div class="mt-5 max-w-md mx-auto sm:flex sm:justify-center md:mt-8">
            <div class="rounded-md shadow">
              <NuxtLink
                  to="#"
                  class="w-full flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 md:py-4 md:text-lg md:px-10">
                Get Started
              </NuxtLink>
            </div>
          </div>
        </div>

        <div class="mt-16">
          <div class="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
            <div v-for="feature in features" :key="feature.name" class="pt-6">
              <div class="flow-root bg-white rounded-lg px-6 pb-8">
                <div class="-mt-6">
                  <div>
                    <span
                        class="inline-flex items-center justify-center p-3 bg-indigo-500 rounded-md shadow-lg">
                      <component :is="feature.icon" class="h-6 w-6 text-white" aria-hidden="true"/>
                    </span>
                  </div>
                  <h3 class="mt-8 text-lg font-medium text-gray-900 tracking-tight">{{
                      feature.name
                    }}</h3>
                  <p class="mt-5 text-base text-gray-500">
                    {{ feature.description }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <footer class="bg-background">
      <div class="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8">
        <p class="text-center text-foreground text-sm">
          &copy; {{ currentYear }} Cortex. All rights reserved. Open source under MIT license.
        </p>
      </div>
    </footer>
  </div>
</template>


<style scoped>
@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
  100% {
    transform: translateY(0px);
  }
}

.flow-root {
  animation: float 6s ease-in-out infinite;
}
</style>