<script setup>
import { onMounted, ref, watch } from 'vue';
import { useOllamaStore } from '~/stores';
import ChatInterface from "~/components/ai/ChatInterface.vue";
import OllamaLoader from "~/components/ai/OllamaLoader.vue";
import CodeEditor from "@cortex/shared/components/CodeEditor.vue";

const ollamaStore = useOllamaStore();
const { isOllamaInstalled, isChecking } = storeToRefs(ollamaStore);

const code = ref('');
const editorContent = ref('');
const editorLanguage = ref('rust');

onMounted(async () => {
  if (isOllamaInstalled.value === null) {
    await ollamaStore.checkOllamaInstallation();
  }
  if (isOllamaInstalled.value) {
    await ollamaStore.setupListeners();
  }
});

watch(code, (newCode) => {
  editorContent.value = newCode;
});
</script>

<template>
  <OllamaLoader v-if="isChecking || isOllamaInstalled === null" />
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