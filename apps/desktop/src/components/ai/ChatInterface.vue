<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { info } from '@tauri-apps/plugin-log'
import Chat from '@cortex/shared/components/ai/chat.vue'
import { useChatStore, useOllamaStore } from '~/stores'

const props = defineProps({
  editorContent: {
    type: String,
    default: ''
  },
  editorLanguage: {
    type: String,
    default: 'unspecified'
  },
  exerciseSlug: {
    type: String,
    required: true
  }
})

const ollamaStore = useOllamaStore()
const chatStore = useChatStore()
const { data } = useAuth()

const { isSending } = storeToRefs(ollamaStore)
const { messages, isStreaming, currentStreamingMessage } = storeToRefs(chatStore)

onMounted(async () => {
  await info('ChatInterface mounted')
  if (messages.value.length === 0) {
    chatStore.addMessage({
      sender: 'ai',
      content: '¡Hola! Soy CORTEX-IA. ¿En qué puedo ayudarte hoy?'
    })
  }
  await ollamaStore.setupListeners()
})

const handleSendMessage = async (message: string) => {
  await info(`Handling message. Exercise slug: ${props.exerciseSlug}, Message: ${message}`)

  if (!message.trim()) {
    await info('Empty message, not sending')
    return
  }

  try {
    await ollamaStore.sendPrompt({
      message,
      exerciseSlug: props.exerciseSlug,
      editorContent: props.editorContent,
      language: props.editorLanguage
    })
  } catch (error) {
    await info('Error in handleSendMessage: ' + error)
    console.error('Error sending prompt:', error)
  }
}

onUnmounted(() => {
  ollamaStore.removeListeners()
})
</script>

<template>
  <Chat
      :messages="messages"
      :is-streaming="isStreaming"
      :current-streaming-message="currentStreamingMessage"
      :is-sending="isSending"
      :avatar-src="data?.avatar_url || 'https://placewaifu.com/image'"
      @send-message="handleSendMessage"
  />
</template>