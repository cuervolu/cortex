<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import ChatMessage from './ChatMessage.vue'
import CortexLogo from "~/components/CortexLogo.vue"
import { useOllamaInteraction } from '@/composables/useOllamaInteraction'
import { useChatStore } from '~/stores'
import { Send } from 'lucide-vue-next'

const props = defineProps({
  editorContent: {
    type: String,
    default: ''
  },
  editorLanguage: {
    type: String,
    default: 'unspecified'
  }
})

const { sendPrompt } = useOllamaInteraction()
const chatStore = useChatStore()
const prompt = ref('')

onMounted(() => {
  if (chatStore.messages.length === 0) {
    chatStore.addMessage({
      sender: 'ai',
      content: '¡Hola! Soy CORTEX-IA. ¿En qué puedo ayudarte hoy?'
    })
  }
})

async function handleSendPrompt() {
  if (!prompt.value.trim()) return
  const userMessage = prompt.value.trim()
  chatStore.addMessage({
    sender: 'user',
    content: userMessage
  })
  prompt.value = ''
  chatStore.setIsSending(true)
  try {
    await sendPrompt(userMessage, 'user-id', props.editorContent, props.editorLanguage)
  } catch (error) {
    if (error instanceof Error) {
      chatStore.setPromptError(error.message)
    } else {
      chatStore.setPromptError('An unknown error occurred')
    }
  } finally {
    chatStore.setIsSending(false)
  }
}
</script>

<template>
  <div class="h-full flex flex-col bg-background rounded-2xl shadow-lg overflow-hidden text-foreground border-2 border-border">
    <div class="p-4 border-b border-border">
      <CortexLogo />
    </div>
    <div class="flex-grow overflow-auto p-6 space-y-4">
      <ChatMessage
          v-for="(message, index) in chatStore.messages"
          :key="index"
          :sender="message.sender"
          :content="message.content"
      />
      <ChatMessage
          v-if="chatStore.isStreaming && chatStore.currentStreamingMessage"
          sender="ai"
          :content="chatStore.currentStreamingMessage"
      />
      <div v-if="chatStore.isStreaming" class="text-sm text-muted-foreground">
        CORTEX-IA está escribiendo...
      </div>
      <div v-if="chatStore.promptError" class="text-sm text-error">{{ chatStore.promptError }}</div>
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
            :disabled="chatStore.isSending || chatStore.isStreaming"
            @click="handleSendPrompt"
        >
          <Send class="h-5 w-5" />
        </Button>
      </div>
    </div>
  </div>
</template>