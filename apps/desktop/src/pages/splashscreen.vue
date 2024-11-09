<script setup lang="ts">
import {invoke} from '@tauri-apps/api/core';
import {open} from '@tauri-apps/plugin-shell';
import {debug, error as logError} from "@tauri-apps/plugin-log";
import {Github, Twitter} from 'lucide-vue-next'
import {AppError} from "@cortex/shared/types";
import DarkLogo from '~/assets/img/icon1_dark.svg'
import CortexLogo from '~/assets/img/icon1.svg'
import SplashImg from '~/assets/img/splash_2.webp'
import WaifuImg from '~/assets/img/splash_1.webp'

const splashImages = [SplashImg, WaifuImg];
const currentImage = ref(splashImages[0]);

definePageMeta({
  auth: false,
  layout: false
})

const initApp = async () => {
  try {
    await debug('Starting application initialization')

    // Simular tiempo de carga
    await new Promise(resolve => setTimeout(resolve, 3000))

    // await invoke('close_splashscreen_show_main')

    await debug('Application initialization completed')
  } catch (error) {
    await logError(`Error during initialization: ${error}`)
    throw new Error('Error during initialization', { cause: error })
  }
}
onMounted(() => {
  currentImage.value = splashImages[Math.floor(Math.random() * splashImages.length)];
  initApp();
});
</script>

<template>
  <div class="w-[580px] h-[400px] grid grid-cols-[245px_335px] overflow-hidden rounded-xl">
    <!-- Left Column -->
    <div class="bg-[#4A1D96] flex flex-col items-center justify-between p-6 text-white relative">
      <!-- Logo -->
      <div class="w-16 h-16 mb-4">
        <img
            :src="DarkLogo"
            alt="Brain Logo"
            width="64"
            height="64"
            class="w-full h-full"
        >
      </div>
      <!-- Loading Text -->
      <div class="text-center space-y-3 mb-8">
        <h1 class="text-xl font-medium max-w-[200px]">
          Espera un momento, estamos cargando tu aventura...
        </h1>
        <p class="text-lg opacity-80 animate-pulse uppercase">Conectando</p>
      </div>
      <!-- Footer -->
      <div class="text-center space-y-2">
        <p class="text-xs opacity-80">
          Problemas de conexión? Háznoslo saber!
        </p>
        <div class="flex gap-3 justify-center">
          <button
              class="flex items-center gap-1.5 hover:opacity-80 transition-opacity text-sm"
              @click="open('https://twitter.com/elonmusk')"
          >
            <Twitter class="w-4 h-4"/>
            <span>Twitter</span>
          </button>
          <button
              class="flex items-center gap-1.5 hover:opacity-80 transition-opacity text-sm"
              @click="open('https://github.com/cuervolu/cortex')"
          >
            <Github class="w-4 h-4"/>
            <span>Github</span>
          </button>
        </div>
      </div>
    </div>
    <!-- Right Column - Illustration -->
    <div class="bg-gradient-to-b from-[#E5F0FF] to-[#C7E1FF]">
      <div class="w-full h-full relative">
        <!-- Main Image -->
        <img
            :src="currentImage"
            alt="Path Illustration"
            class="w-full h-full object-contain"
        >
      </div>
    </div>
  </div>
</template>