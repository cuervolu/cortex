<script setup lang="ts">
import { ref, watch } from 'vue'
import { useOllamaInteraction } from '~/composables/useOllamaInteraction'
import type { Message } from "~/types/message"
import ChatInput from "~/components/ai/ChatInput.vue"

const { prompt, response, isSending, sendPrompt } = useOllamaInteraction()
const messages = ref<Message[]>([])
const currentAiMessage = ref<Message | null>(null)

watch(response, (newResponse) => {
  if (newResponse && newResponse.trim() !== '') {
    if (!currentAiMessage.value) {
      // Crear un nuevo mensaje de IA solo si no existe uno actual
      currentAiMessage.value = { id: Date.now(), text: newResponse, sender: 'ai' }
      messages.value.push(currentAiMessage.value)
    } else {
      // Actualizar el mensaje existente
      currentAiMessage.value.text = newResponse
    }
  }
})

watch(isSending, (newIsSending) => {
  if (!newIsSending) {
    // La respuesta ha terminado, reiniciar currentAiMessage
    currentAiMessage.value = null
  }
})

const handleSend = async (message: string) => {
  if (message.trim() === '') return
  const userMessage: Message = { id: Date.now(), text: message, sender: 'user' }
  messages.value.push(userMessage)
  prompt.value = message
  await sendPrompt()
}

const handleClear = () => {
  messages.value = []
  currentAiMessage.value = null
}
</script>

<template>
  <div class="flex h-screen bg-background text-foreground">
    <!-- Sidebar -->
    <SideBar @clear-chat="handleClear" />
    <!-- Chat area -->
    <div class="flex-1 flex flex-col">
      <ScrollArea class="flex-1 p-4">
        <MessageList
            :messages="messages"
            :is-loading="isSending"
        />
      </ScrollArea>
      <ChatInput
          :is-sending="isSending"
          @send="handleSend"
      />
    </div>
  </div>
</template>