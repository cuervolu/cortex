<script setup lang="ts">
import {ref} from 'vue'
import {getVersion} from '@tauri-apps/api/app'

type Theme = 'light' | 'dark' | 'system'
type Font = 'system' | 'arial' | 'helvetica' | 'times'

const theme = ref<Theme>('light')
const font = ref<Font>('system')
const appVersion = await getVersion()
</script>


<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-xl font-semibold mb-4">Theme</h2>
      <RadioGroup v-model="theme">
        <div class="flex items-center space-x-2">
          <RadioGroupItem id="light" value="light"/>
          <Label for="light">Light</Label>
        </div>
        <div class="flex items-center space-x-2">
          <RadioGroupItem id="dark" value="dark"/>
          <Label for="dark">Dark</Label>
        </div>
        <div class="flex items-center space-x-2">
          <RadioGroupItem id="system" value="system"/>
          <Label for="system">System</Label>
        </div>
      </RadioGroup>
    </div>

    <div>
      <h2 class="text-xl font-semibold mb-4">Font</h2>
      <Select v-model="font">
        <SelectTrigger class="w-[180px]">
          <SelectValue placeholder="Select a font"/>
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="system">System Default</SelectItem>
          <SelectItem value="arial">Arial</SelectItem>
          <SelectItem value="helvetica">Helvetica</SelectItem>
          <SelectItem value="times">Times New Roman</SelectItem>
        </SelectContent>
      </Select>
    </div>

    <div>
      <h2 class="text-xl font-semibold mb-4">App Version</h2>
      <p>{{ appVersion }}</p>
    </div>
  </div>
</template>

