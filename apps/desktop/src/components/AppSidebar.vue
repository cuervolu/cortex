<script setup lang="ts">
import { Home, Map, BookOpen, HelpCircle } from 'lucide-vue-next'

const { isOpen } = defineProps<{
  isOpen: boolean
}>()

const sidebarItems = [
  { icon: Home, label: "Inicio", to: "/" },
  { icon: Map, label: "Roadmaps", to: "/roadmaps" },
  { icon: BookOpen, label: "Cursos", to: "/cursos" },
  { icon: HelpCircle, label: "Soporte", to: "/soporte" },
]
</script>

<template>
  <aside
      :class="[
      'transition-all duration-300 ease-in-out flex flex-col',
      isOpen ? 'w-64 translate-x-0' : 'w-0 -translate-x-full',
      'fixed md:relative z-30 h-full bg-muted overflow-hidden'
    ]"
  >
    <div class="w-64 flex flex-col h-full">
      <div class="flex items-center p-4 h-16">
        <img
            src="~/assets/img/Cortex%20Logo.svg"
            alt="Cortex Logo"
            class="w-8 h-8 mr-2"
        >
        <h1 class="text-xl font-bold">Cortex</h1>
      </div>
      <ScrollArea class="flex-grow">
        <nav class="p-4 space-y-2">
          <NuxtLink
              v-for="(item, index) in sidebarItems"
              :key="index"
              :to="item.to"
              class="flex items-center w-full px-2 py-2 text-sm font-medium rounded-md hover:bg-primary/10 hover:text-primary"
          >
            <component :is="item.icon" class="mr-2 h-4 w-4"/>
            {{ item.label }}
          </NuxtLink>
        </nav>
      </ScrollArea>
      <div class="p-4 mt-auto">
        <Separator class="my-4"/>
        <div class="flex items-center space-x-4">
          <img
              src="https://placewaifu.com/image/100"
              alt="User Avatar"
              class="w-10 h-10 rounded-full flex-shrink-0"
          >
          <div class="flex flex-col min-w-0">
            <span class="font-medium truncate">John Doe</span>
            <span class="text-sm text-muted-foreground truncate">john@example.com</span>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>