import { defineStore } from 'pinia'
import { ref } from 'vue'

interface Message {
  sender: 'ai' | 'user'
  content: string
}

export const useChatStore = defineStore('chat', () => {
  const messages = ref<Message[]>([])
  const currentStreamingMessage = ref('')
  const isStreaming = ref(false)
  const isSending = ref(false)
  const promptError = ref<string | null>(null)

  const addMessage = (message: Message) => {
    messages.value.push(message)
  }

  const updateStreamingMessage = (content: string | ((prev: string) => string)) => {
    if (typeof content === 'function') {
      currentStreamingMessage.value = content(currentStreamingMessage.value)
    } else {
      currentStreamingMessage.value = content
    }
  }

  const finishAIMessage = () => {
    if (currentStreamingMessage.value) {
      addMessage({
        sender: 'ai',
        content: currentStreamingMessage.value
      })
      currentStreamingMessage.value = ''
    }
    setIsStreaming(false)
  }

  const clearStreamingMessage = () => {
    currentStreamingMessage.value = ''
  }

  const setIsStreaming = (value: boolean) => {
    isStreaming.value = value
  }

  const setIsSending = (value: boolean) => {
    isSending.value = value
  }

  const setPromptError = (error: string | null) => {
    promptError.value = error
  }

  return {
    messages,
    currentStreamingMessage,
    isStreaming,
    isSending,
    promptError,
    addMessage,
    updateStreamingMessage,
    finishAIMessage,
    clearStreamingMessage,
    setIsStreaming,
    setIsSending,
    setPromptError,
  }
})