<script setup lang="ts">
import { Home, BarChart2, GitFork, BookOpen, HelpCircle } from 'lucide-vue-next'

interface NavItem {
  icon: typeof Home
  label: string
  to: string
}

const navItems: NavItem[] = [
  { icon: Home, label: "Dashboard", to: "/" },
  { icon: BarChart2, label: "Analytics", to: "/analytics" },
  { icon: GitFork, label: "Roadmaps", to: "/roadmaps" },
  { icon: BookOpen, label: "Courses", to: "/courses" },
  { icon: HelpCircle, label: "Support", to: "/support" },
]
</script>

<template>
  <aside class="w-64 bg-purple-900 bg-opacity-50 text-white p-6 flex flex-col">
    <div class="flex items-center mb-8">
      <img src="~/assets/img/Cortex%20Logo.svg" alt="Cortex Logo" class="w-8 h-8 mr-2">
      <h1 class="text-xl font-bold">CORTEX</h1>
    </div>
    <nav class="flex-grow">
      <h2 class="text-xs uppercase text-purple-300 mb-4">Navigation</h2>
      <ul class="space-y-2">
        <li v-for="(item, index) in navItems" :key="index">
          <NuxtLink
              :to="item.to"
              :class="`flex items-center p-2 rounded-lg hover:bg-purple-800 transition-colors ${
              index === 0 ? 'bg-purple-800' : ''
            }`"
          >
            <component :is="item.icon" class="w-5 h-5 mr-3" />
            {{ item.label }}
          </NuxtLink>
        </li>
      </ul>
    </nav>
    <div class="mt-auto pt-6 border-t border-purple-700">
      <div class="flex items-center">
        <Avatar class="w-10 h-10 mr-3">
          <AvatarImage src="/placeholder.svg" alt="Alex Williamson" />
          <AvatarFallback>AW</AvatarFallback>
        </Avatar>
        <div>
          <h3 class="font-medium">Alex Williamson</h3>
          <p class="text-sm text-purple-300">#alexwil227</p>
        </div>
      </div>
    </div>
  </aside>
</template>