<script setup>
import {onMounted, ref, watch} from 'vue';
import ChatInterface from "~/components/ai/ChatInterface.vue";
import OllamaLoader from "~/components/ai/OllamaLoader.vue";
import {useOllamaDetection} from '@/composables/useOllamaDetection';
import CodeEditor from "@cortex/shared/components/ui/CodeEditor.vue";
import {Menu} from "lucide-vue-next";

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
  <OllamaLoader v-if="isOllamaInstalled === null"/>
  <div v-else-if="isOllamaInstalled" class="flex h-screen bg-background relative">
    <div class="absolute top-4 right-4 z-10 lg:hidden">
      <Sheet>
        <SheetTrigger as-child>
          <Button variant="ghost" size="icon">
            <Menu/>
          </Button>
        </SheetTrigger>
        <SheetContent>
          <ChatInterface
              :editor-content="editorContent"
              :editor-language="editorLanguage"
          />
        </SheetContent>
      </Sheet>
    </div>
    <div class="flex-grow p-4 h-full max-w-full overflow-auto">
      <CodeEditor
          v-model="code"
          placeholder="// Type some code here
console.log('Hello, CORTEX-IA!');
// Your code will be analyzed by our AI"
          language="rust"
          class="h-full"
      />
    </div>
    <div class="hidden lg:block max-w-md w-1/2 p-4 h-full justify-end">
      <ChatInterface
          :editor-content="editorContent"
          :editor-language="editorLanguage"
      />
    </div>
  </div>
  <div v-else class="flex items-center justify-center h-screen bg-background">
    <p class="text-error">Ollama no está instalado. Por favor, instálelo para continuar.</p>
  </div>
</template>

<style scoped>
.flex-grow {
  flex-shrink: 1;
  min-width: 0;
}
</style>
