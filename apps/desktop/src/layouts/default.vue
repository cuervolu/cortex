<script setup lang="ts">
import { ref } from 'vue'

const isSidebarOpen = ref(true)

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}
</script>

<template>
  <div class="flex h-screen w-full bg-background text-foreground overflow-hidden">
    <AppSidebar :is-open="isSidebarOpen" />
    <div class="flex-1 flex flex-col min-w-0 w-full">
      <AppHeader :is-sidebar-open="isSidebarOpen" @toggle-sidebar="toggleSidebar" />
      <main class="flex-1 overflow-hidden">
        <ScrollArea class="h-full p-6">
          <div class="space-y-4">
            <slot />
          </div>
        </ScrollArea>
      </main>
      <AppFooter />
    </div>
  </div>
</template>