<script setup lang="ts">
import updateRocket from '~/assets/img/update-rocket.svg'
import type { UpdateProgress } from '~/composables/useUpdater'

defineProps<{
  version: string
  isUpdating: boolean
  progress: UpdateProgress
  onViewDetails: () => void
  onInstall: () => void
}>()
</script>

<template>
  <div class="flex flex-col h-full">
    <CardHeader class="text-center space-y-1 pb-5">
      <CardTitle class="text-4xl font-semibold text-white">
        Nueva versión disponible
      </CardTitle>
      <CardDescription class="flex flex-col items-center gap-1">
        <span class="text-white">Versión {{ version }}</span>
        <button
            class="text-sm text-[#d9d9d9] underline hover:text-white transition-colors"
            @click="onViewDetails"
        >
         Ver detalles
        </button>
      </CardDescription>
    </CardHeader>
    <div class="flex-1 relative">
      <div class="absolute inset-0">
        <img
            class="w-full h-full object-cover"
            :src="updateRocket"
            alt="Update rocket illustration"
        >
      </div>
      <div class="relative z-10 flex flex-col h-full justify-end">
        <p class="text-[#5e4a6e] text-center px-4 mb-8">
          Actualiza para obtener nuevas funciones.
        </p>
        <CardFooter class="flex flex-col gap-3 px-8 pb-8">
          <div v-if="isUpdating && progress.total" class="w-full">
            <div class="h-2 bg-white/20 rounded-full overflow-hidden">
              <div
                  class="h-full bg-[#8b2a93] transition-all duration-300"
                  :style="{ width: `${(progress.downloaded / progress.total) * 100}%` }"
              />
            </div>
            <p class="text-center text-white text-sm mt-2">
              Descargando: {{ Math.round((progress.downloaded / progress.total) * 100) }}%
            </p>
          </div>
          <div class="flex w-full gap-3">
            <Button
                class="flex-1 py-2.5 px-5 rounded-md bg-[#8b2a93] text-white text-sm font-medium hover:bg-[#7a2580] transition-colors uppercase"
                :disabled="isUpdating"
                @click="onInstall"
            >
              {{ isUpdating ? 'Instalando...' : '¡Descargar Ahora!' }}
            </Button>
            <DialogClose as-child>
              <Button
                  class="flex-1 py-2.5 px-5 rounded-md bg-[#d9d9d9] border border-[#8c2a94] text-[#8c2a94] text-sm font-medium hover:bg-[#c9c9c9] transition-colors"
                  :disabled="isUpdating"
              >
                Recordar más tarde
              </Button>
            </DialogClose>
          </div>
        </CardFooter>
      </div>
    </div>
  </div>
</template>