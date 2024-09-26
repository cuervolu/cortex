<script setup lang="ts">

defineProps<{
  sender: 'user' | 'ai'
}>()
</script>

<template>
  <Avatar>
    <AvatarFallback>{{ sender === 'ai' ? 'AI' : 'TU' }}</AvatarFallback>
    <AvatarImage :src="sender === 'ai' ? 'https://placewaifu.com/image/200' : 'https://placewaifu.com/image/300'"/>
  </Avatar>
</template>

