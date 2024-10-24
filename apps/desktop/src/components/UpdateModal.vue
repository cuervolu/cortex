<script setup lang="ts">
import { useUpdater } from '~/composables/useUpdater'

const showDetails = ref(false)
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

const handleViewDetails = () => {
  showDetails.value = true
}

const handleBackToMain = () => {
  showDetails.value = false
}

const modalClass = computed(() => {
  return {
    'w-[654px] h-[580px] rounded-[14.442px] border-[1.203px] border-zinc-200 relative overflow-hidden flex flex-col': true,
    'bg-gradient-to-r from-[rgba(80,23,106,0.68)] to-[rgba(168,61,173,0.84)] shadow-[0px_4px_68.8px_0px_rgba(0,0,0,0.25)] backdrop-blur-[16.9px]': !showDetails.value,
    'bg-[linear-gradient(77deg,rgba(80,23,106,0.68)_5.2%,rgba(168,61,173,0.84)_97.62%)] shadow-[0px_4px_68.8px_0px_rgba(0,0,0,0.25)] backdrop-blur-[16.9px]': showDetails
  }
})

const handleInstall = () => {
  installUpdate()
}

onMounted(async () => {
  const update = await checkForUpdates()
  if (update) {
    open.value = true
  }
})
</script>

<template>
  <Dialog v-model:open="isUpdateAvailable">
    <DialogContent class="p-0 border-none bg-transparent w-[654px]">
      <Card :class="modalClass">
        <UpdateNotification
            v-if="!showDetails"
            :version="updateVersion"
            :is-updating="isUpdating"
            :progress="progress"
            :on-view-details="handleViewDetails"
            :on-install="handleInstall"
        />
        <UpdateDetails
            v-else
            :version="updateVersion"
            :date="updateDate"
            :notes="updateNotes"
            :on-back="handleBackToMain"
        />
      </Card>
    </DialogContent>
  </Dialog>
</template>