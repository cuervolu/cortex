import {defineStore} from 'pinia'
import {ref} from 'vue'
import {invoke} from '@tauri-apps/api/core'
import {listen, type UnlistenFn} from '@tauri-apps/api/event'
import {info, error as logError} from '@tauri-apps/plugin-log'
import {useChatStore} from './useChat.store'

interface SendChatParams {
  message: string
  exerciseSlug: string
  editorContent?: string
  language?: string
}

export const useOllamaStore = defineStore('ollama', () => {
  const isSending = ref(false)
  const error = ref<string | null>(null)

  let unlistenResponse: UnlistenFn | null = null
  let unlistenResponseEnd: UnlistenFn | null = null

  const setupListeners = async () => {
    const chatStore = useChatStore()

    try {
      unlistenResponse = await listen<string>('ollama-response', async (event) => {
        chatStore.updateStreamingMessage(prev => prev + event.payload)
        chatStore.setIsStreaming(true)
      })

      unlistenResponseEnd = await listen('ollama-response-end', async () => {
        chatStore.finishAIMessage()
        isSending.value = false
      })

      await info('Ollama listeners set up successfully')
    } catch (error) {
      await logError('Error setting up listeners: ' + error)
    }
  }

  const removeListeners = () => {
    if (unlistenResponse) unlistenResponse()
    if (unlistenResponseEnd) unlistenResponseEnd()
  }

  const sendPrompt = async ({
                              message,
                              exerciseSlug,
                              editorContent,
                              language
                            }: SendChatParams) => {

    if (!message.trim() || !exerciseSlug) {
      await info(`Message: ${message}, exerciseSlug: ${exerciseSlug}`)
      await info('Invalid message or missing exercise slug')
      return
    }

    const chatStore = useChatStore()
    isSending.value = true
    error.value = null

    try {
      await info('Adding user message to chat')
      chatStore.addMessage({
        sender: 'user',
        content: message
      })

      chatStore.clearStreamingMessage()
      chatStore.setIsStreaming(true)

      let fullPrompt = message
      if (editorContent?.trim()) {
        fullPrompt = `User's code (${language || 'unspecified language'}):\n\`\`\`${language || ''}\n${editorContent}\n\`\`\`\n\nUser's question: ${message}`
      }
      
      await invoke('send_chat_prompt', {
        exerciseSlug,
        message: fullPrompt
      })

      await info('Chat prompt sent successfully')
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'An unknown error occurred'
      error.value = errorMessage
      chatStore.setPromptError(errorMessage)
      isSending.value = false
      await logError('Failed to send chat prompt: ' + errorMessage)
    }
  }

  return {
    isSending,
    error,
    setupListeners,
    removeListeners,
    sendPrompt
  }
})