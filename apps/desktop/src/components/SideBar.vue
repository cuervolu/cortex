<script setup lang="ts">
import { Trash2 } from 'lucide-vue-next'

defineEmits(['clear-chat'])
</script>
<template>
  <div class="w-64 bg-muted/60 text-foreground p-4 border-r">
    <h2 class="text-xl font-bold mb-4">Chat con IA</h2>
    <Button class="w-full" variant="outline" @click="$emit('clear-chat')">
      <Trash2 class="mr-2 h-4 w-4" /> Limpiar chat
    </Button>
  </div>
</template>

