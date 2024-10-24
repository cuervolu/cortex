<script setup lang="ts">
import {error as logError} from '@tauri-apps/plugin-log'
import { useUserStore } from '~/stores'
import { useUpdater } from '~/composables/useUpdater'
import '~/assets/css/global.css'

useColorMode()

const userStore = useUserStore()
const { checkForUpdates } = useUpdater()

onMounted(async () => {
  await userStore.initStore()

  // Only check for updates in the client (Tauri)
  if (import.meta.client) {
    try {
      await checkForUpdates()
      
      const CHECK_INTERVAL = 1000 * 60 * 60 // 1 hour
      setInterval(async () => {
        try {
          await checkForUpdates()
        } catch (error) {
         await logError(`Failed to check for updates: ${error}`)
        }
      }, CHECK_INTERVAL)
    } catch (error) {
      await logError(`Failed to check for updates: ${error}`)
    }
  }
})
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>