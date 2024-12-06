<script setup lang="ts">
import { invoke } from '@tauri-apps/api/core';
import { open } from '@tauri-apps/plugin-shell';
import { debug, error as logError } from "@tauri-apps/plugin-log";
import TwitterIcon from '~/components/icons/TwitterIcon.vue';
import GithubIcon from '~/components/icons/GithubIcon.vue';
import { AppError } from "@cortex/shared/types";
import CortexLogo from '~/assets/img/cortex_logo_dark_mode.svg'
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
    await new Promise(resolve => setTimeout(resolve, 8000))

    await invoke('close_splashscreen_show_main')

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
  <section class="w-screen h-screen flex overflow-hidden rounded-[22px] splash-background p-2.5">
    <div class="flex flex-col gap-2.5">
      <div class="w-12 h-12 bg-[#1d2127] rounded-lg justify-center items-center">
        <img :src="CortexLogo" alt="Brain Logo" class="w-full h-full p-2.5" />
      </div>
      <div class="flex flex-col gap-3 py-7 justify-center items-center">
        <img :src="CortexLogo" alt="Brain Logo" class="w-20 swing" />
        <div class="flex flex-col gap-2.5 justify-center items-center text-[white]">
          <span class="text-base text-center font-semibold tracking-wider">Espera un momento estamos cargando tu
            aventura...</span>
          <span class="text-[#c0c0c0] text-center text-sm font-normal">CONECTANDO</span>
        </div>
      </div>
      <div class="flex flex-col justify-end items-center gap-2 h-full pb-3">
        <span class="text-white text-center text-xs font-normal">Problemas de Conexión? Háznoslo saber!</span>
        <div class="flex gap-4 text-[#53B7BE]">
          <div class="flex items-center gap-2 cursor-pointer"
            @click="open('https://x.com/i/status/1854958268952854543')">
            <div class="w-[18px] h-[18px] flex items-center p-[2px] rounded-[2px] bg-[#d9d9d9]">
              <TwitterIcon :width="15" class="fill-[#1D1D1D]" />
            </div>
            <span class="text-sm">Twitter</span>
          </div>
          <div class="flex items-center gap-2 cursor-pointer" @click="open('https://github.com/cuervolu/cortex')">
            <div class="w-[18px] h-[18px] flex items-center p-[2px] rounded-[2px] bg-[#d9d9d9]">
              <GithubIcon :width="15" class="fill-[#1D1D1D]" />
            </div>
            <span class="text-sm">Github</span>
          </div>
        </div>
      </div>
    </div>
    <div class="min-w-[245px] relative overflow-hidden rounded-xl">
      <img :src="currentImage" alt="" class="w-full h-full object-cover">
      <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent" />
    </div>

  </section>
</template>

<style scoped>
.splash-background {
  background: linear-gradient(114deg, #381653 18.8%, #60246F 80.96%);
}

.swing {
  -webkit-transform-origin: top center;
  transform-origin: top center;
  -webkit-animation-name: swing;
  animation-name: swing;

  /* Duración total de cada ciclo (1s de animación + 2s de pausa) */
  -webkit-animation-duration: 3s;
  animation-duration: 3s;

  /* Repetir indefinidamente */
  -webkit-animation-iteration-count: infinite;
  animation-iteration-count: infinite;

  /* Mantener la última posición de la animación antes de la siguiente ejecución */
  -webkit-animation-fill-mode: forwards;
  animation-fill-mode: forwards;
}

@keyframes swing {
  0% {
    transform: rotate3d(0, 0, 1, 0deg);
  }
  10% {
    transform: rotate3d(0, 0, 1, 15deg);
  }
  20% {
    transform: rotate3d(0, 0, 1, -10deg);
  }
  30% {
    transform: rotate3d(0, 0, 1, 5deg);
  }
  40% {
    transform: rotate3d(0, 0, 1, -5deg);
  }
  50% {
    transform: rotate3d(0, 0, 1, 0deg);
  }
  100% {
    transform: rotate3d(0, 0, 1, 0deg);
  }
}

</style>