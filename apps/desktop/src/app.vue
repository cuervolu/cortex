<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '~/stores'

const userStore = useUserStore()

onMounted(async () => {
  await userStore.initStore()
})
</script>

<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>