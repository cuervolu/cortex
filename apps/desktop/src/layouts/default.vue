<script lang="ts" setup>
import { ref } from 'vue';
import AppSidebar from '~/components/AppSidebar.vue';
import TitleBar from '~/components/TitleBar.vue';

const isCollapsed = ref(false);

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value;
}
</script>

<template>
  <div class="w-screen h-screen flex flex-col rounded-lg">
    <div class="flex-1 flex flex-col overflow-hidden screen-background rounded-lg">
      <TitleBar
          class="z-50"
          :has-sidebar="true"
          :is-collapsed="isCollapsed"
          @toggle-sidebar="toggleSidebar"
      />
      <div class="flex-1 flex gap-2.5 p-5 overflow-hidden">
        <AppSidebar
            :is-collapsed="isCollapsed"
            @toggle-sidebar="toggleSidebar"
        />
        <main class="grow shrink basis-0 self-stretch bg-background rounded-[34px] flex-col justify-start items-start gap-2.5 inline-flex overflow-auto scrollable-content">
          <slot />
        </main>
      </div>
    </div>
  </div>
</template>

<style>
.screen-background {
  background: conic-gradient(from -29deg at 57.08% 50.06%, #381653 0deg, #6C319A 95.39999485015869deg, #D085A5 153.00000429153442deg, #1D848F 217.80000686645508deg, #381653 333.0000042915344deg);
}
</style>