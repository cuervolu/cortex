<!-- EditorSettings.vue -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { cn } from "@cortex/shared/lib/utils";

const fontSize = ref<number>(14)
const lineNumbers = ref<boolean>(true)
const wordWrap = ref<boolean>(true)

// Computed property to convert fontSize to an array for Slider
const fontSizeArray = computed({
  get: () => [fontSize.value],
  set: (newValue: number[]) => {
    if (newValue.length > 0) {
      fontSize.value = newValue[0]
    }
  }
})
</script>

<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-xl font-semibold mb-4">Font Size</h2>
      <Slider
          v-model="fontSizeArray"
          :max="24"
          :min="8"
          :step="1"
          :class="cn('w-[200px]')"
      />
      <span class="ml-2">{{ fontSize }}px</span>
    </div>

    <div class="space-y-2">
      <div class="flex items-center space-x-2">
        <Switch
            id="line-numbers"
            v-model="lineNumbers"
        />
        <Label for="line-numbers">Show Line Numbers</Label>
      </div>

      <div class="flex items-center space-x-2">
        <Switch
            id="word-wrap"
            v-model="wordWrap"
        />
        <Label for="word-wrap">Word Wrap</Label>
      </div>
    </div>
  </div>
</template>