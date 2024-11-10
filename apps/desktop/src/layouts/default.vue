<script lang="ts" setup>
import AppSidebar from '~/components/AppSidebar.vue';
import TitleBar from '~/components/TitleBar.vue';
import {useUserStore} from "~/stores";
import {useUpdater} from "~/composables/useUpdater";
import {error as logError} from "@tauri-apps/plugin-log";

const isCollapsed = ref(false);

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value;
}

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
  <div class="w-screen h-screen flex flex-col rounded-lg">
    <div class="flex-1 flex flex-col overflow-hidden screen-background rounded-lg">
      <TitleBar
          class="z-50"
          :has-sidebar="true"
          :is-collapsed="isCollapsed"
          :is-auth="false"
          @toggle-sidebar="toggleSidebar"
      />
      <div class="flex-1 flex gap-2.5 p-5 overflow-hidden">
        <AppSidebar
            :is-collapsed="isCollapsed"
            @toggle-sidebar="toggleSidebar"
        />
        <UpdateModal
            v-model:open="isUpdateAvailable"
            :version="updateVersion"
            :notes="updateNotes"
            :date="updateDate"
            :is-updating="isUpdating"
            :progress="progress"
            @install="installUpdate"
        />
        <main class="grow shrink basis-0 self-stretch bg-background rounded-[34px] flex-col justify-start items-start gap-2.5 inline-flex overflow-auto scrollable-content">
          <slot />
        </main>
      </div>
    </div>
  </div>
</template>

<style>
.screen-background {
  background: conic-gradient(from -29deg at 57.08% 50.06%, #381653 0deg, #6C319A 95.39999485015869deg, #D085A5 153.00000429153442deg, #1D848F 217.80000686645508deg, #381653 333.0000042915344deg);
}
</style>