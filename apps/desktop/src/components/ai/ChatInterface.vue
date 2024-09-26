<script lang="ts" setup>
import { ref, watch, onMounted } from 'vue'
import ChatMessage from './ChatMessage.vue'
import CortexLogo from "~/components/CortexLogo.vue"
import { useOllamaInteraction } from '@/composables/useOllamaInteraction'
import { Send } from 'lucide-vue-next'

interface Message {
  sender: 'ai' | 'user'
  content: string
}

const { prompt, response, isSending, isStreaming, promptError, sendPrompt } = useOllamaInteraction()
const messages = ref<Message[]>([])
const currentStreamingMessage = ref('')

onMounted(() => {
  messages.value.push({
    sender: 'ai',
    content: '¡Hola! Soy CORTEX-IA. ¿En qué puedo ayudarte hoy?'
  })
})

watch([response, isStreaming], ([newResponse, newIsStreaming]) => {
  if (newIsStreaming) {
    currentStreamingMessage.value = newResponse
  } else if (newResponse.trim()) {
    const lastMessage = messages.value[messages.value.length - 1]
    if (lastMessage && lastMessage.sender === 'ai') {
      lastMessage.content = newResponse
    } else {
      messages.value.push({
        sender: 'ai',
        content: newResponse
      })
    }
    currentStreamingMessage.value = ''
  }
})

async function handleSendPrompt() {
  if (!prompt.value.trim()) return
  messages.value.push({
    sender: 'user',
    content: prompt.value
  })
  await sendPrompt('user-id')
}
</script>

<template>
  <div class="h-full flex flex-col bg-background rounded-2xl shadow-lg overflow-hidden text-foreground border-2 border-border">
    <div class="p-4 border-b border-border">
      <CortexLogo />
    </div>
    <div class="flex-grow overflow-auto p-6 space-y-4">
      <ChatMessage
          v-for="(message, index) in messages"
          :key="index"
          :sender="message.sender"
          :content="message.content"
      />
      <ChatMessage
          v-if="isStreaming || currentStreamingMessage.trim()"
          sender="ai"
          :content="currentStreamingMessage"
      />
      <div v-if="isStreaming" class="text-sm text-muted-foreground">
        CORTEX-IA está escribiendo...
      </div>
      <div v-if="promptError" class="text-sm text-error">{{ promptError }}</div>
    </div>
    <div class="p-4 bg-muted/20">
      <div class="relative flex items-center">
       <Textarea
           v-model="prompt"
           class="w-full pr-16 py-3 px-4 rounded-xl bg-background border border-border focus:outline-none focus:ring-2 focus:ring-primary resize-none min-h-[48px] max-h-[200px] overflow-y-auto"
           rows="1"
           placeholder="Escribe tu mensaje aquí..."
           @keyup.enter.prevent="handleSendPrompt"
       />

        <Button
            size="icon"
            class="absolute right-3 h-10 w-10 flex items-center justify-center rounded-lg"
            :disabled="isSending || isStreaming"
            @click="handleSendPrompt"
        >
          <Send class="h-5 w-5" />
        </Button>
      </div>
    </div>
  </div>
</template>
