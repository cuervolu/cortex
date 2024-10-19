<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { invoke } from '@tauri-apps/api/core';
import { useOllamaStore } from '~/stores';
import { error as logError } from "@tauri-apps/plugin-log";

definePageMeta({
  auth: false,
  layout: false
})

const progress = ref(0);
const statusMessage = ref('Initializing...');
const ollamaStore = useOllamaStore();

async function initApp() {
  try {
    // Check Ollama installation
    statusMessage.value = 'Checking Ollama installation...';
    await ollamaStore.checkOllamaInstallation();
    progress.value = 20;

    if (!ollamaStore.isOllamaInstalled) {
      statusMessage.value = 'Ollama is not installed. Please install it to continue.';
      return;
    }

    // Initialize Ollama models
    statusMessage.value = 'Initializing Ollama models...';
    await invoke('init_ollama_models');
    progress.value = 100;

    // Close splashscreen and show main window
    await invoke('close_splashscreen_show_main');
  } catch (error) {
    await logError(`Error during initialization: ${error}`);
    statusMessage.value = 'Error during initialization. Please restart the app.';
  }
}

onMounted(() => {
  initApp();
});
</script>

<template>
  <div class="flex flex-col items-center justify-center h-screen bg-gray-900">
    <div class="text-center">
      <img src="https://media2.giphy.com/media/mcsPU3SkKrYDdW3aAU/200w.gif?cid=6c09b952avkmj5tw6ix2obahm2ty3669lbtnlump2sx2fl4z&ep=v1_gifs_search&rid=200w.gif&ct=g" alt="Cortex Logo" class="mx-auto w-32 h-32 mb-4">
      <h1 class="text-2xl font-bold text-white mb-2">Cortex</h1>
      <p class="text-gray-400 mb-4">{{ statusMessage }}</p>
      <div class="w-64 mb-4">
        <Progress :model-value="progress" />
      </div>
      <p v-if="ollamaStore.checkError" class="text-red-500 mt-2">{{ ollamaStore.checkError }}</p>
    </div>
  </div>
</template>