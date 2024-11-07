<script setup lang="ts">
import {Menu, X, ChevronLeft, Settings} from 'lucide-vue-next'

const {isSidebarOpen} = defineProps<{
  isSidebarOpen: boolean
}>()
const emit = defineEmits<{
  (e: 'toggleSidebar'): void
}>()
</script>

<template>
  <header class="bg-background text-foreground p-4 flex items-center h-16">
    <Button variant="ghost" size="icon" class="mr-2" @click="emit('toggleSidebar')">
      <X v-if="isSidebarOpen" class="h-6 w-6"/>
      <Menu v-else class="h-6 w-6"/>
    </Button>
    <div class="flex-1 flex items-center">
      <div class="flex items-center">
        <ChevronLeft class="h-6 w-6 mr-2"/>
        <h2 class="text-lg font-semibold">Back to Exercise</h2>
      </div>
      <div class="flex-1 flex items-center justify-center">
        <span
            class="text-sm hidden md:inline text-center">Opperators and Expresions / Gigasecond</span>
      </div>
      <Button variant="ghost" size="icon">
        <NuxtLink to="/settings" class="h-6 w-6">
          <Settings/>
        </NuxtLink>
      </Button>
    </div>
  </header>
</template>