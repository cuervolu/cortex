<script setup lang="ts">
import { useOllamaDetection } from '~/composables/useOllamaDetection'
import { onMounted } from 'vue'

const { isOllamaInstalled, isChecking, checkError, checkOllamaInstallation } = useOllamaDetection()

onMounted(() => {
  checkOllamaInstallation()
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
      <Button variant="outline" @click="checkOllamaInstallation">
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