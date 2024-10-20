<script lang="ts" setup>
import {ref, onMounted} from 'vue'
import {storeToRefs} from 'pinia'
import ChatMessage from './ChatMessage.vue'
import CortexLogo from "~/components/CortexLogo.vue"
import {useChatStore, useOllamaStore} from '~/stores'
import {Send} from 'lucide-vue-next'
import ModelSelector from "~/components/ai/ModelSelector.vue"

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

const ollamaStore = useOllamaStore()
const chatStore = useChatStore()
const {isSending, isStreaming, promptError, currentStreamingMessage} = storeToRefs(ollamaStore)
const {messages} = storeToRefs(chatStore)

const prompt = ref('')
const selectedModel = ref('')

onMounted(() => {
  if (messages.value.length === 0) {
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

  try {
    await ollamaStore.sendPrompt({
      message: userMessage,
      userId: 'user-id',
      editorContent: props.editorContent,
      language: props.editorLanguage,
      selectedModel: selectedModel.value
    })
  } catch (error) {
    console.error('Error sending prompt:', error)
  }
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' && !event.ctrlKey && !event.shiftKey) {
    event.preventDefault()
    handleSendPrompt()
  }
}
</script>

<template>
  <div
      class="h-full flex flex-col bg-background rounded-2xl shadow-lg overflow-hidden text-foreground border-2 border-border"
  >
    <div class="p-4 border-b border-border flex justify-between items-center">
      <CortexLogo/>
      <ModelSelector v-model="selectedModel"/>
    </div>
    <div class="flex-grow overflow-auto p-6 space-y-4">
      <ChatMessage
          v-for="(message, index) in messages"
          :key="index"
          :sender="message.sender"
          :content="message.content"
      />
      <ChatMessage
          v-if="isStreaming && currentStreamingMessage"
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
            class="w-full pr-16 py-3 px-4 rounded-xl bg-background border border-border focus:outline-none focus:ring-2 focus:ring-primary resize-none"
            placeholder="Escribe tu mensaje aquí..."
            @keydown="handleKeydown"
        />
        <Button
            size="icon"
            class="absolute right-3 h-10 w-10 flex items-center justify-center rounded-lg"
            :disabled="isSending || isStreaming || !selectedModel"
            @click="handleSendPrompt"
        >
          <Send class="h-5 w-5"/>
        </Button>
      </div>
    </div>
  </div>
</template>