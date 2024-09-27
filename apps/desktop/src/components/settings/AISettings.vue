<script setup lang="ts">
import { ref, watch } from 'vue'
import OllamaModels from "~/components/settings/OllamaModels.vue";

type AIProvider = 'claude' | 'chatgpt' | 'ollama'

const aiProvider = ref<AIProvider>('claude')
const apiKey = ref<string>('')

watch(aiProvider, (newValue) => {
  if (newValue !== 'ollama') {
    // Reset selected Ollama model when changing AI provider
    // You might want to implement this logic in the OllamaModels component
  }
})
</script>


<template>
  <div class="space-y-6">
    <div>
      <h2 class="text-xl font-semibold mb-4">AI Provider</h2>
      <RadioGroup v-model="aiProvider">
        <div class="flex items-center space-x-2">
          <RadioGroupItem id="claude" value="claude" />
          <Label for="claude">Claude</Label>
        </div>
        <div class="flex items-center space-x-2">
          <RadioGroupItem id="chatgpt" value="chatgpt" />
          <Label for="chatgpt">ChatGPT</Label>
        </div>
        <div class="flex items-center space-x-2">
          <RadioGroupItem id="ollama" value="ollama" />
          <Label for="ollama">Ollama</Label>
        </div>
      </RadioGroup>
    </div>

    <div v-if="aiProvider === 'claude' || aiProvider === 'chatgpt'">
      <h2 class="text-xl font-semibold mb-4">API Key</h2>
      <Input
          v-model="apiKey"
          type="password"
          placeholder="Enter your API key"
      />
    </div>

    <OllamaModels v-if="aiProvider === 'ollama'" />
  </div>
</template>

