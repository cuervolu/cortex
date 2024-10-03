<script setup lang="ts">
import {useOllamaStore} from '~/stores/useOllama.store';
import OllamaLoader from "~/components/ai/OllamaLoader.vue";

const ollamaStore = useOllamaStore();
const {isOllamaInstalled, isChecking} = storeToRefs(ollamaStore);

onMounted(async () => {
  if (isOllamaInstalled.value === null) {
    await ollamaStore.checkOllamaInstallation();
  }
});

</script>

<template>
  <div class="flex items-center justify-center h-screen">
    <OllamaLoader v-if="isChecking || isOllamaInstalled === null"/>
    <div v-else-if="isOllamaInstalled" class="text-center">
      <Button as-child
      >
        <NuxtLink to="/exercises">
          Ver ejercicios

        </NuxtLink>
      </Button>
    </div>
    <div v-else class="text-center">
      <p class="text-error mb-4">Ollama no está instalado. Por favor, instálelo para continuar.</p>
    </div>
  </div>
</template>