<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useOllamaStore } from '~/stores'
import { onMounted } from 'vue'

const ollamaStore = useOllamaStore()
const { isOllamaInstalled, isChecking, checkError } = storeToRefs(ollamaStore)

onMounted(() => {
  ollamaStore.checkOllamaInstallation()
})
</script>

<template>
  <div class="flex flex-col items-center justify-center h-screen bg-background">
    <div v-if="isChecking" class="text-center">
      <div class="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"/>
      <p class="mt-4 text-foreground">Verificando la instalación de Ollama...</p>
    </div>
    <div v-else-if="checkError" class="text-center text-error">
      <p>{{ checkError }}</p>
      <Button variant="outline" @click="ollamaStore.checkOllamaInstallation">
        Reintentar
      </Button>
    </div>
    <div v-else-if="isOllamaInstalled === false" class="text-center text-error">
      <p>Ollama no está instalado. Por favor, instálelo para continuar.</p>
      <Button href="https://ollama.ai">
        Descargar Ollama
      </Button>
    </div>
  </div>
</template>