<script lang="ts" setup>
interface Props {
  name: string;
  icon: Component;
  isSelected: boolean;
  isCollapsed: boolean;
}

defineProps<Props>();
defineEmits(['click']);
</script>

<template>
  <button
      :class="[
      'min-w-[50px] p-[5px] rounded-[27px] justify-start items-center gap-2.5 flex w-full transition-all duration-300',
      isSelected ? 'navigation-button-selected' : '',
      !isCollapsed ? 'self-stretch' : ''
    ]"
      @click="$emit('click')"
  >
    <div
        class="min-w-10 h-10 p-2 from-white to-white rounded-[20px] justify-center items-center gap-2.5 flex navigation-button-icon-background"
    >
      <component :is="icon" class="w-[18.79px] h-[19.84px]"/>
    </div>
    <div
        v-if="!isCollapsed"
        :class="['text-base font-semibold', isSelected ? 'text-[#f4f8f7]' : 'text-[#f4f8f7]/60']"
    >
      {{ name }}
    </div>
  </button>
</template>

<style scoped>
.navigation-button-selected {
  background: linear-gradient(95deg, rgba(255, 255, 255, 0.11) 17.86%, rgba(255, 255, 255, 0.07) 92.41%);
}

.navigation-button-icon-background {
  background: linear-gradient(148deg, rgba(255, 255, 255, 0.30) -12.12%, rgba(255, 255, 255, 0.10) 124.25%);
}
</style>