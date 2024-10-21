<script setup lang="ts">
interface FooterSectionProps {
  title: string
  items: string[]
}

const props = defineProps<FooterSectionProps>()
</script>

<template>
  <div class="flex overflow-hidden flex-col whitespace-nowrap">
    <h2 class="text-base font-semibold text-slate-900">{{ props.title }}</h2>
    <ul class="flex flex-col self-start mt-6 text-sm font-light leading-6 text-gray-500">
      <li v-for="(item, index) in props.items" :key="index" :class="{ 'mt-4': index > 0 }">
        <NuxtLink to="#" :aria-label="item">{{ item }}</NuxtLink>
      </li>
    </ul>
  </div>
</template>