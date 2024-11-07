<script setup lang="ts">
import { getCurrentWindow } from '@tauri-apps/api/window';
import { platform } from '@tauri-apps/plugin-os';
import { PanelRightOpen, Minus, Maximize, X } from 'lucide-vue-next';
import TitlebarMenu from '~/components/TitlebarMenu.vue';

const props = defineProps({
  hasSidebar: {
    type: Boolean,
    default: true,
  },
  isCollapsed: {
    type: Boolean,
    required: false,
  },
  isAuth: {
    type: Boolean,
    required: false,
    default: false,
  },
});

const currentPlatform = await platform();
const appWindow = getCurrentWindow();
const emit = defineEmits(['toggle-sidebar']);
</script>

<template>
  <header
    data-tauri-drag-region
    class="flex justify-between items-center h-10 px-3"
    :class="[
      currentPlatform !== 'windows' && !isAuth 
        ? 'bg-popover/80' 
        : 'bg-popover/20'
    ]"
  >
    <nav class="flex items-center ml-5">
      <img
        data-tauri-drag-region
        src="~/assets/img/Cortex%20Logo.svg"
        alt="Cortex Logo"
        class="w-5 h-5 mr-2"
      >
      <Button
        v-if="props.hasSidebar"
        size="sm"
        variant="ghost"
        class="p-1 hover:bg-slate-50/20"
        @click="emit('toggle-sidebar')"
      >
        <PanelRightOpen
          width="20"
          height="20"
          :class="[
            'transition-transform duration-300',
            { 'rotate-180': !isCollapsed, [isAuth ? 'text-foreground' : 'text-white']: true }
          ]"
        />
      </Button>
    </nav>
    <h1
      class="text-base font-semibold tracking-widest" :class="isAuth ? 'text-foreground' : 'text-white'"
      data-tauri-drag-region
    >
      Cortex
    </h1>
    <div class="flex items-center h-full mr-5">
      <TitlebarMenu :is-auth="isAuth" />
      <Separator orientation="vertical" class="bg-white/20" />
      <Button
        size="sm"
        variant="ghost"
        class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded hover:bg-slate-50/20"
        @click="appWindow.minimize()"
      >
        <Minus class="w-4 h-4" :class="isAuth ? 'text-foreground' : 'text-white'" />
      </Button>
      <Button
        size="sm"
        variant="ghost"
        class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded hover:bg-slate-50/20"
        @click="appWindow.toggleMaximize()"
      >
        <Maximize width="20" height="20" :class="isAuth ? 'text-foreground' : 'text-white'" />
      </Button>
      <Button
        size="sm"
        variant="ghost"
        class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded hover:bg-red-500/80"
        @click="appWindow.close()"
      >
        <X width="20" height="20" :class="isAuth ? 'text-foreground' : 'text-white'" />
      </Button>
    </div>
  </header>
</template>

<style scoped>
.titlebar-button:last-child {
  margin-right: 6px;
}
</style>
