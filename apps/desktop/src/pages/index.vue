<script setup>
import {onMounted, ref, watch} from 'vue';
import ChatInterface from "~/components/ai/ChatInterface.vue";
import OllamaLoader from "~/components/ai/OllamaLoader.vue";
import {useOllamaDetection} from '@/composables/useOllamaDetection';
import CodeEditor from "@cortex/shared/components/ui/CodeEditor.vue";

const {isOllamaInstalled, checkOllamaInstallation} = useOllamaDetection();
const code = ref('');
const editorContent = ref('');
const editorLanguage = ref('rust');

onMounted(() => {
  if (isOllamaInstalled.value === null) {
    checkOllamaInstallation();
  }
});

watch(code, (newCode) => {
  editorContent.value = newCode;
});
</script>

<template>
  <OllamaLoader v-if="isOllamaInstalled === null" />
  <div v-else-if="isOllamaInstalled" class="flex h-full w-full">
    <div class="flex-grow overflow-hidden">
      <CodeEditor
          v-model="code"
          placeholder="// Type some code here
console.log('Hello, CORTEX-IA!');
// Your code will be analyzed by our AI"
          language="rust"
          class="h-full"
      />
    </div>
    <div class="w-[400px] h-full overflow-hidden">
      <ChatInterface
          :editor-content="editorContent"
          :editor-language="editorLanguage"
      />
    </div>
  </div>
  <div v-else class="flex items-center justify-center h-full">
    <p class="text-error">Ollama no está instalado. Por favor, instálelo para continuar.</p>
  </div>
</template>

<style scoped>
.flex-grow {
  flex: 1;
  min-width: 0;
}
</style>