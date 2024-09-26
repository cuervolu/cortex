<script setup lang="ts">
import { ref } from 'vue'
import { Send } from 'lucide-vue-next'

const props = defineProps<{
  isSending: boolean
}>()

const emit = defineEmits<{
  (e: 'send', message: string): void
}>()

const inputValue = ref('')

const handleSend = () => {
  if (inputValue.value.trim() !== '') {
    emit('send', inputValue.value)
    inputValue.value = ''
  }
}
</script>

<template>
  <div class="p-4 bg-muted/40 border-t">
    <div class="flex space-x-2">
      <Input
          v-model="inputValue"
          placeholder="Escribe tu mensaje aquí..."
          @keydown.enter="handleSend"
      />
      <Button :disabled="isSending" @click="handleSend">
        <Send class="h-4 w-4" />
      </Button>
    </div>
  </div>
</template>