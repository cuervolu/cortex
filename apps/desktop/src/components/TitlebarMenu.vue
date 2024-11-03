<script setup lang="ts">
import { getVersion } from '@tauri-apps/api/app'
import { open } from '@tauri-apps/plugin-shell';
import {
  ChevronDown,
  Github,
  LifeBuoy,
  Settings,
} from 'lucide-vue-next'

interface Props {
  isAuth: boolean;
}

const {isAuth} = defineProps<Props>();

const appVersion = await getVersion();

const openGithub = async() => {
  await open('https://github.com/cuervolu/cortex')
}

const openSupport = async() => {
  await open('https://github.com/cuervolu/cortex')
}
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button
          size="sm"
          variant="ghost"
          class="titlebar-button flex items-center justify-center w-8 h-8 mx-1 p-0 rounded hover:bg-slate-50/20"
      >
        <ChevronDown width="20" height="20" :class="isAuth ? 'text-foreground' : 'text-white'" />
      </Button>
    </DropdownMenuTrigger>

    <DropdownMenuContent class="w-56">
      <DropdownMenuLabel>Cortex</DropdownMenuLabel>
      <DropdownMenuSeparator />

      <DropdownMenuGroup>
        <DropdownMenuItem @click="openGithub">
          <Github class="mr-2 h-4 w-4" />
          <span>GitHub</span>
        </DropdownMenuItem>

        <DropdownMenuItem @click="openSupport">
          <LifeBuoy class="mr-2 h-4 w-4" />
          <span>Soporte</span>
        </DropdownMenuItem>

        <DropdownMenuItem>
          <Settings class="mr-2 h-4 w-4" />
          <span>Configuración</span>
        </DropdownMenuItem>
      </DropdownMenuGroup>

      <DropdownMenuSeparator />
      <DropdownMenuItem disabled>
        <span>Versión {{ appVersion }}</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>