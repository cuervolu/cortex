<script setup lang="ts">
import { useUserStore } from '~/stores'
import { useUpdater } from '~/composables/useUpdater'
import {error as logError} from "@tauri-apps/plugin-log";
import '~/assets/css/global.css'

useColorMode()
const userStore = useUserStore()

const {
  isUpdateAvailable,
  updateVersion,
  updateNotes,
  updateDate,
  isUpdating,
  progress,
  checkForUpdates,
  installUpdate
} = useUpdater()

onMounted(async () => {
  await userStore.initStore()
    try {
      await checkForUpdates()
      const CHECK_INTERVAL = 1000 * 60 * 60
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
})
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>

  <UpdateModal
      v-model:open="isUpdateAvailable"
      :version="updateVersion"
      :notes="updateNotes"
      :date="updateDate"
      :is-updating="isUpdating"
      :progress="progress"
      @install="installUpdate"
  />
</template>