<script setup lang="ts">
import { getCurrentWindow } from '@tauri-apps/api/window';
import { PanelRightOpen, ChevronDown, Minus, Maximize, X } from "lucide-vue-next";
const props = defineProps({
  hasSidebar: {
    type: Boolean,
    default: true
  },
  isCollapsed: {
    type: Boolean,
    required: true
  }
});
const appWindow = getCurrentWindow();
const emit = defineEmits(['toggle-sidebar']);
</script>

<template>
  <header
      data-tauri-drag-region
      class="flex justify-between items-center bg-white/20 h-10 px-3"
  >
    <nav class="flex items-center ml-5">
      <img data-tauri-drag-region src="~/assets/img/Cortex%20Logo.svg" alt="Cortex Logo" class="w-5 h-5 mr-2">
      <Button v-if="props.hasSidebar" size="sm" variant="ghost" class="p-1" @click="emit('toggle-sidebar')">
        <PanelRightOpen
            width="20"
            height="20"
            :class="['transition-transform duration-300 text-foreground', { 'rotate-180': !isCollapsed }]"
        />
      </Button>
    </nav>
    <h1 class="text-base font-semibold text-foreground" data-tauri-drag-region>Cortex</h1>
    <div class="flex h-full mr-5">
      <Button size="sm" variant="ghost" class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded">
        <ChevronDown width="20" height="20" class="text-foreground" />
      </Button>
      <Button size="sm" variant="ghost" class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded" @click="appWindow.minimize()">
        <Minus class="w-4 h-4"/>
      </Button>
      <Button size="sm" variant="ghost"  class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded" @click="appWindow.toggleMaximize()">
        <Maximize width="20" height="20" class="text-foreground" />
      </Button>
      <Button size="sm" variant="ghost" class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded  hover:bg-red-500" @click="appWindow.close()">
        <X width="20" height="20" class="text-foreground" />
      </Button>
    </div>
  </header>
</template>

<style scoped>
.titlebar-button:last-child {
  margin-right: 6px;
}
</style>