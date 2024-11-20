<script setup lang="ts">
import {ref, onMounted, onUnmounted} from 'vue'
import {Loader2} from 'lucide-vue-next'

const currentMessage = ref(0)
const funnyMessages = [
  "¡Espera un momento! Estamos enseñando a los monos a programar...",
  "Convenciendo a los píxeles de que se comporten...",
  "Reticulando splines y confundiendo a los profesores de geometría...",
  "Calentando el condensador de flujo...",
  "Generando mensajes ingeniosos de carga...",
  "Demostrando que P = NP (esto podría tardar un poco)...",
  "Dividiendo por cero. ¡Ups!",
  "Descargando más RAM...",
  "Desenredando tus audífonos. ¿Cómo se enredaron así?",
  "Contando hasta el infinito. Dos veces.",
]

let interval: NodeJS.Timeout

onMounted(() => {
  interval = setInterval(() => {
    currentMessage.value = (currentMessage.value + 1) % funnyMessages.length
  }, 3000)
})

onUnmounted(() => {
  clearInterval(interval)
})
</script>

<template>
  <div
      class="fixed inset-0 bg-gradient-to-br from-purple-400 via-pink-500 to-red-500 flex items-center justify-center z-50">
    <div class="text-white p-8 max-w-2xl w-full space-y-8">
      <div class="flex items-center justify-center">
        <Loader2 class="h-24 w-24 text-yellow-300 animate-spin"/>
      </div>
      <h2 class="text-5xl font-bold text-center text-yellow-300 animate-pulse">
        Loading...
      </h2>
      <p class="text-3xl text-center font-semibold animate-bounce">
        {{ funnyMessages[currentMessage] }}
      </p>
      <div class="flex justify-center space-x-4">
        <div
            v-for="i in 5"
            :key="i"
            :class="[
            'w-6 h-6 rounded-full transition-all duration-300 ease-in-out',
            i - 1 === currentMessage % 5 ? 'bg-yellow-300 scale-125' : 'bg-white/50'
          ]"
        />
      </div>
      <div class="absolute top-10 left-10 animate-bounce">
        <div class="w-16 h-16 bg-green-400 rounded-full opacity-75"/>
      </div>
      <div class="absolute bottom-10 right-10 animate-ping">
        <div class="w-12 h-12 bg-blue-400 rounded-full opacity-75"/>
      </div>
      <div class="absolute top-1/4 right-1/4 animate-pulse">
        <div class="w-8 h-8 bg-red-400 rounded-full opacity-75"/>
      </div>
      <div class="absolute bottom-1/4 left-1/4 animate-spin">
        <div class="w-10 h-10 bg-yellow-400 rounded-full opacity-75"/>
      </div>
    </div>
  </div>
</template>