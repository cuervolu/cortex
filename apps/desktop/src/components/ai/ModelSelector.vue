<script setup lang="ts">
import {useOllamaModels} from '~/composables/useOllamaModels'

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const {models, isLoading, error, fetchOllamaModels} = useOllamaModels()

const selectedModel = ref(props.modelValue)

watch(selectedModel, (newValue) => {
  emit('update:modelValue', newValue)
})

// Re-fetch models if an error occurred
watch(error, async (newError) => {
  if (newError) {
    await fetchOllamaModels()
  }
})
</script>

<template>
  <div>
    <Select v-model="selectedModel">
      <SelectTrigger class="w-[180px]">
        <SelectValue :placeholder="isLoading ? 'Loading models...' : 'Select a model'"/>
      </SelectTrigger>
      <SelectContent>
        <SelectGroup>
          <SelectLabel>Ollama Models</SelectLabel>
          <SelectItem v-for="model in models" :key="model.name" :value="model.name">
            {{ model.name }}
          </SelectItem>
        </SelectGroup>
      </SelectContent>
    </Select>
    <p v-if="error" class="text-sm text-red-500 mt-2">{{ error }}</p>
  </div>
</template>

<style scoped>

</style>