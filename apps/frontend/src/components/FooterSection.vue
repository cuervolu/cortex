<script setup lang="ts">
interface FooterSectionProps {
  title: string
  items: {
    text: string
    link?: string
  }[]
}

const props = defineProps<FooterSectionProps>()
</script>

<template>
  <div class="flex overflow-hidden flex-col whitespace-nowrap">
    <h2 class="text-base font-semibold text-slate-900 dark:text-[#FAF9F7]">{{ props.title }}</h2>
    <ul class="flex flex-col self-start mt-6 text-sm font-light leading-6 text-gray-500 dark:text-[#BEBEBE]">
      <li v-for="(item, index) in props.items" :key="index" :class="{ 'mt-4': index > 0 }">
        <NuxtLink :to="item.link || '#'" :aria-label="item.text">{{ item.text }}</NuxtLink>
      </li>
    </ul>
  </div>
</template>