<script setup lang="ts">
import { useUserStore } from '~/stores'
import '~/assets/css/global.css'

useColorMode()
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