<script setup lang="ts">
import { ImagePlus } from 'lucide-vue-next';
import type { Component } from 'vue';

interface Props {
  previewImage: string | null;
  isDragging: boolean;
  iconComponent?: Component;
}

defineProps<Props>();
defineEmits<{
  (e: 'upload', event?: MouseEvent): void;
}>();
</script>

<template>
  <div
      class="w-full h-60 relative overflow-hidden rounded-lg group cursor-pointer transition-all"
      @click="$emit('upload')"
  >
    <div
        class="w-full h-full rounded-md border-2 border-dashed transition-all duration-200"
        :class="{
        'border-muted-foreground/50 hover:border-muted-foreground': !isDragging && !previewImage,
        'border-primary bg-primary/5': isDragging,
        'border-none': previewImage
      }"
    >
      <img
          v-if="previewImage"
          :src="previewImage"
          alt="Content cover"
          class="w-full h-62 object-cover"
      >
      <div
          v-else
          class="absolute inset-0 flex flex-col items-center justify-center gap-2 text-muted-foreground"
          :class="{ 'text-primary': isDragging }"
      >
        <ImagePlus class="w-8 h-8"/>
        <p class="text-sm font-medium text-center">
          {{ isDragging ? 'Suelta la imagen aquí' : 'Arrastra y suelta o da click para agregar una portada' }}
        </p>
      </div>
    </div>

    <!-- Floating Edit Button -->
    <div
        v-if="previewImage"
        class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity"
    >
      <Button
          variant="secondary"
          size="sm"
          class="absolute right-4 top-4"
          @click.stop="$emit('upload')"
      >
        <ImagePlus class="w-4 h-4 mr-2"/>
        Cambiar portada
      </Button>
    </div>

    <!-- Icon Badge -->
    <div v-if="iconComponent" class="absolute left-8 bottom-8">
      <div class="w-12 h-12 bg-white/90 backdrop-blur-sm rounded-lg shadow-lg p-2 transition-all duration-200">
        <component :is="iconComponent" class="w-8 h-8" fill="#28282B"/>
      </div>
    </div>
  </div>
</template>