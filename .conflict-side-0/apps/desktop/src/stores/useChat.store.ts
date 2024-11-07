import {invoke} from '@tauri-apps/api/core'
import {listen} from '@tauri-apps/api/event'
import type {ChatState, ExerciseContext} from '~/types'
import {debug, info} from "@tauri-apps/plugin-log";
import {AppError} from "@cortex/shared/types";

export const useChatStore = defineStore('chat', () => {
  const state = reactive<ChatState>({
    provider: {
      name: 'none',
      requiresApiKey: false
    },
    messages: [],
    currentStreamingMessage: '',
    isStreaming: false,
    isSending: false,
    error: null
  })

  const aiProviderStore = useAIProviderStore();
  const errorHandler = useDesktopErrorHandler()
  let unlistenStream: (() => void) | null = null
  let unlistenStreamEnd: (() => void) | null = null
  let lastChunk: string = ''

  const setupListeners = async (providerName: string) => {
    try {
      removeListeners()
      lastChunk = ''

      const streamEvent = `${providerName}-stream`
      const streamEndEvent = `${providerName}-stream-end`

      unlistenStream = await listen<string>(streamEvent, (event) => {
        state.isStreaming = true
        const chunk = event.payload

        if (chunk !== lastChunk) {
          state.currentStreamingMessage += chunk
          lastChunk = chunk
        }
      })

      unlistenStreamEnd = await listen(streamEndEvent, () => {
        if (state.currentStreamingMessage) {
          state.messages.push({
            sender: 'ai',
            content: state.currentStreamingMessage,
            timestamp: new Date().toISOString()
          })
        }
        state.currentStreamingMessage = ''
        state.isStreaming = false
        state.isSending = false
        lastChunk = ''
      })

      await info(`Set up listeners for ${providerName}: ${streamEvent}, ${streamEndEvent}`)
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: {provider: providerName},
        fatal: false
      })
    }
  }


  const removeListeners = () => {
    if (unlistenStream) {
      unlistenStream()
      unlistenStream = null
    }
    if (unlistenStreamEnd) {
      unlistenStreamEnd()
      unlistenStreamEnd = null
    }
  }


  const setProvider = async (providerName: string) => {
    if (!providerName) {
      throw new AppError('Invalid provider name', {
        statusCode: 400,
        silent: true
      })
    }

    const provider = aiProviderStore.getProvider(providerName)
    if (!provider) {
      throw new AppError(`Unknown provider: ${providerName}`, {
        statusCode: 404,
        data: {providerName},
        silent: true
      })
    }

    await debug(`Setting up provider: ${providerName}`)

    if (provider.requiresApiKey) {
      try {
        await aiProviderStore.checkProviderConfiguration(providerName)
        if (!provider.isConfigured) {
          throw new AppError('Provider not configured', {
            statusCode: 401,
            data: {provider: providerName}
          })
        }
        await invoke('set_provider', {providerName})
      } catch (error) {
        await debug(`Error configuring provider: ${error}`)
        await aiProviderStore.setProviderConfigured(providerName, false)
        throw new AppError('Provider requires API key configuration', {
          statusCode: 401,
          silent: true
        })
      }
    } else {
      await invoke('set_provider', {providerName})
    }

    state.provider.name = providerName
    state.provider.requiresApiKey = provider.requiresApiKey
    await setupListeners(providerName)

    await debug(`Provider ${providerName} set up successfully`)
  }

  const sendMessage = async (exerciseId: string, content: string) => {
    try {
      if (state.provider.requiresApiKey) {
        const provider = aiProviderStore.getProvider(state.provider.name)
        if (!provider?.isConfigured) {
          throw new AppError(`${state.provider.name} requires API key configuration`, {
            statusCode: 401,
            silent: true
          })
        }
      }

      state.isSending = true
      state.isStreaming = true
      state.error = null
      state.currentStreamingMessage = ''

      state.messages.push({
        sender: 'user',
        content,
        timestamp: new Date().toISOString()
      })

      await invoke('send_message', {
        exerciseId,
        message: content
      })
    } catch (error) {
      state.isStreaming = false
      state.isSending = false
      throw error
    }
  }


  const startSession = async (context: ExerciseContext) => {
    try {
      await invoke('start_exercise_session', {context})
      state.messages = []
      state.currentStreamingMessage = ''
      state.error = null
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: {exerciseId: context.exercise_id}
      })
    }
  }

  const endSession = async (exerciseId: string) => {
    try {
      await invoke('end_exercise_session', {exerciseId})
      state.messages = []
      state.currentStreamingMessage = ''
      removeListeners()
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: {exerciseId}
      })
    }
  }


  const clearChat = () => {
    state.messages = []
    state.currentStreamingMessage = ''
    state.isStreaming = false
    state.isSending = false
    state.error = null
  }

  // Cleanup on unmount
  onUnmounted(() => {
    removeListeners()
  })

  return {
    // State
    messages: computed(() => state.messages),
    currentStreamingMessage: computed(() => state.currentStreamingMessage),
    isStreaming: computed(() => state.isStreaming),
    isSending: computed(() => state.isSending),
    error: computed(() => state.error),
    provider: computed(() => state.provider),

    // Actions
    setProvider,
    sendMessage,
    startSession,
    endSession,
    clearChat,
    removeListeners
  }
})