<script setup lang="ts">
import { ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useOllamaStore } from '~/stores'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const ollamaStore = useOllamaStore()
const { isLoading, error } = storeToRefs(ollamaStore)

const selectedModel = ref(props.modelValue)

watch(selectedModel, (newValue) => {
  emit('update:modelValue', newValue)
})

// Fetch local models when the component is mounted
onMounted(async () => {
  await ollamaStore.fetchLocalModels()
})

// Re-fetch models if an error occurred
watch(error, async (newError) => {
  if (newError) {
    await ollamaStore.fetchLocalModels()
  }
})

const localModels = computed(() => ollamaStore.localModels)
</script>

<template>
  <div>
    <Select v-model="selectedModel">
      <SelectTrigger class="w-[180px]">
        <SelectValue :placeholder="isLoading ? 'Loading models...' : 'Select a model'"/>
      </SelectTrigger>
      <SelectContent>
        <SelectGroup>
          <SelectLabel>Local Ollama Models</SelectLabel>
          <SelectItem v-for="model in localModels" :key="model.name" :value="model.name">
            {{ model.name }}
          </SelectItem>
        </SelectGroup>
      </SelectContent>
    </Select>
    <p v-if="error" class="text-sm text-red-500 mt-2">{{ error }}</p>
  </div>
</template>