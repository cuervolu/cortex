<script setup lang="ts">
defineProps<{
  imageUrl: string | null
  title: string
}>()
</script>

<template>
  <Card class="mb-6 overflow-hidden">
    <img
        :src="imageUrl || '/placeholder.svg?height=300&width=1200'"
        :alt="title"
        class="h-[300px] w-full object-cover"
    >
  </Card>
</template>