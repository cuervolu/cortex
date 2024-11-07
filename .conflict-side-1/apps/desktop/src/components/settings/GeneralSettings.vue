<script setup lang="ts">
import { ref } from 'vue'
import { getVersion } from '@tauri-apps/api/app'

type Font = 'system' | 'arial' | 'helvetica' | 'times'

const colorMode = useColorMode()
const font = ref<Font>('system')
const appVersion = await getVersion()

const themes = [
  { value: 'light', label: 'Light' },
  { value: 'dark', label: 'Dark' },
  { value: 'system', label: 'System' }
]

const fonts = [
  { value: 'system', label: 'System Default' },
  { value: 'arial', label: 'Arial' },
  { value: 'helvetica', label: 'Helvetica' },
  { value: 'times', label: 'Times New Roman' }
]
</script>

<template>
  <div class="space-y-8">
    <div>
      <h2 class="text-xl font-semibold mb-4">Theme</h2>
      <RadioGroup v-model="colorMode.preference" class="flex space-x-4">
        <div v-for="theme in themes" :key="theme.value" class="flex items-center space-x-2">
          <RadioGroupItem :id="theme.value" :value="theme.value" />
          <Label :for="theme.value">{{ theme.label }}</Label>
        </div>
      </RadioGroup>
    </div>

    <div>
      <h2 class="text-xl font-semibold mb-4">Font</h2>
      <Select v-model="font">
        <SelectTrigger class="w-[200px]">
          <SelectValue placeholder="Select a font" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem v-for="option in fonts" :key="option.value" :value="option.value">
            {{ option.label }}
          </SelectItem>
        </SelectContent>
      </Select>
    </div>

    <div>
      <h2 class="text-xl font-semibold mb-4">App Version</h2>
      <p class="text-sm">{{ appVersion }}</p>
    </div>
  </div>
</template>

<style scoped>
.dark {
  @apply bg-gray-900 text-white;
}

:deep(.radio-group-item) {
  @apply w-5 h-5 rounded-full border-2 border-primary;
}

:deep(.radio-group-item[data-state="checked"]) {
  @apply bg-primary;
}

:deep(.select-trigger) {
  @apply bg-background border border-input hover:bg-accent hover:text-accent-foreground;
}

:deep(.select-content) {
  @apply bg-popover text-popover-foreground;
}

:deep(.select-item) {
  @apply text-sm;
}
</style>