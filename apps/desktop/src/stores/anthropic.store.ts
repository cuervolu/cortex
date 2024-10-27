import { defineStore } from 'pinia'
import { invoke } from '@tauri-apps/api/core'
import { useStronghold } from '~/composables/useStronghold'

interface ChatResponse {
  text: string
  usage: {
    input_tokens: number
    output_tokens: number
  }
}

interface Message {
  role: 'user' | 'assistant'
  content: string
}

export interface ChatContext {
  exerciseTitle: string
  exerciseInstructions: string
  exerciseHints?: string
  editorContent?: string
  editorLanguage?: string
}

export const useAnthropicStore = defineStore('anthropic', () => {
  const isConfigured = ref(false)
  const isLoading = ref(false)
  const totalTokens = ref(0)
  const messages = ref<Message[]>([])
  const error = ref<string | null>(null)

  const stronghold = useStronghold()
  const userStore = useUserStore()

  const setKeyConfigured = (value: boolean) => {
    isConfigured.value = value
  }

  const checkKeyStatus = async () => {
    try {
      isConfigured.value = await stronghold.isKeyConfigured()
    } catch (e) {
      console.error('Error checking key status:', e)
      isConfigured.value = false
    }
  }

  const sendMessage = async (content: string, context: ChatContext) => {
    try {
      isLoading.value = true
      error.value = null

      const apiKey = await stronghold.getApiKey()
      const user = await userStore.getUser()

      if (!user) {
        throw new Error('Usuario no autenticado')
      }

      if (!apiKey) {
        throw new Error('API key no configurada')
      }

      messages.value.push({ role: 'user', content })

      const response = await invoke<ChatResponse>('chat_with_claude', {
        username: user.username,
        exerciseTitle: context.exerciseTitle,
        exerciseInstructions: context.exerciseInstructions,
        exerciseHints: context.exerciseHints,
        message: content,
        editorContent: context.editorContent,
        editorLanguage: context.editorLanguage,
        apiKey
      })

      messages.value.push({ role: 'assistant', content: response.text })
      totalTokens.value += response.usage.input_tokens + response.usage.output_tokens

      return response
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error desconocido'
      throw e
    } finally {
      isLoading.value = false
    }
  }
  const sendMessageWithHistory = async (content: string, context: ChatContext) => {
    try {
      isLoading.value = true
      error.value = null

      const apiKey = await stronghold.getApiKey()
      const user = await userStore.getUser()

      if (!user) {
        throw new Error('Usuario no autenticado')
      }

      const messageHistory = messages.value.map(msg => [msg.role, msg.content])
      messageHistory.push(['user', content])

      const response = await invoke<ChatResponse>('chat_with_history', {
        username: user.username,
        exerciseTitle: context.exerciseTitle,
        exerciseInstructions: context.exerciseInstructions,
        exerciseHints: context.exerciseHints,
        messages: messageHistory,
        editorContent: context.editorContent,
        editorLanguage: context.editorLanguage,
        apiKey
      })

      messages.value.push({ role: 'user', content })
      messages.value.push({ role: 'assistant', content: response.text })
      totalTokens.value += response.usage.input_tokens + response.usage.output_tokens

      return response
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Error desconocido'
      throw e
    } finally {
      isLoading.value = false
    }
  }

  const clearChat = () => {
    messages.value = []
    totalTokens.value = 0
    error.value = null
  }

  return {
    isConfigured,
    isLoading,
    totalTokens,
    messages,
    error,
    setKeyConfigured,
    checkKeyStatus,
    sendMessage,
    sendMessageWithHistory,
    clearChat
  }
})