<script setup>
import {onMounted} from 'vue'
import ChatInterface from "~/components/ai/ChatInterface.vue";
import OllamaLoader from "~/components/ai/OllamaLoader.vue";
import {useOllamaDetection} from '@/composables/useOllamaDetection'
import CodeEditor from "@cortex/shared/components/ui/CodeEditor.vue";


const {isOllamaInstalled, checkOllamaInstallation} = useOllamaDetection()
const code = ref('')

onMounted(() => {
  if (isOllamaInstalled.value === null) {
    checkOllamaInstallation()
  }
})
</script>

<template>
  <OllamaLoader v-if="isOllamaInstalled === null"/>
  <div v-else-if="isOllamaInstalled" class="flex h-screen bg-background">
    <div class="w-1/2 p-4 h-full"> 
      <CodeEditor
          v-model="code"
          placeholder="// Type some code here
console.log('Hello, CORTEX-IA!');
// Your code will be analyzed by our AI"
          language="rust"
          class="h-full"
      />
    </div>
    <div class="w-1/2 p-4 h-full"> 
      <ChatInterface class="h-full"/> 
    </div>
  </div>
  <div v-else class="flex items-center justify-center h-screen bg-background">
    <p class="text-error">Ollama no está instalado. Por favor, instálelo para continuar.</p>
  </div>
</template>

<style scoped>
</style>