<script setup lang="ts">
import { getVersion, getName } from "@tauri-apps/api/app"
import { ref } from 'vue'
import { Wifi, WifiOff } from 'lucide-vue-next'
import GithubIcon from "~/components/icons/GithubIcon.vue"

const isSystemSynced = ref(true)
const toggleSystemSync = () => {
  isSystemSynced.value = !isSystemSynced.value
}

const appVersion = await getVersion()
const appName = await getName()
</script>

<template>
  <footer class="bg-muted border-t py-2 px-4">
    <div class="flex justify-end items-center space-x-4 text-sm">
      <Button
          variant="ghost"
          size="sm"
          class="p-0 h-auto"
          @click="toggleSystemSync"
      >
        <Wifi v-if="isSystemSynced" class="h-4 w-4 text-green-500"/>
        <WifiOff v-else class="h-4 w-4 text-red-500"/>
        <span class="ml-1">System Monitor</span>
      </Button>
      <span>{{ appName }} v{{ appVersion }}</span>
      <a href="https://github.com" target="_blank" rel="noopener noreferrer" class="text-foreground hover:text-primary">
        <GithubIcon class="h-4 w-4" />
      </a>
    </div>
  </footer>
</template>