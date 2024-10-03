<script lang="ts" setup>
import { ref } from 'vue';
import AppSidebar from '~/components/AppSidebar.vue';

const isCollapsed = ref(false);

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value;
}
</script>

<template>
  <div class="w-screen h-screen p-5 rounded-[55px] justify-start items-start gap-2.5 inline-flex screen-background">
    <app-sidebar
        :is-collapsed="isCollapsed"
        @toggle-sidebar="toggleSidebar"
    />
    <main class="grow shrink basis-0 self-stretch p-[30px] bg-background rounded-[34px] flex-col justify-start items-start gap-2.5 inline-flex">
      <slot />
    </main>
  </div>
</template>

<style>
.screen-background {
  background: conic-gradient(from -29deg at 57.08% 50.06%, #381653 0deg, #6C319A 95.39999485015869deg, #D085A5 153.00000429153442deg, #1D848F 217.80000686645508deg, #381653 333.0000042915344deg);
}
</style>