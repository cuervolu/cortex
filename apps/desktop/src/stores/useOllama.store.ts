import {defineStore} from 'pinia'
import {ref, computed} from 'vue'
import {invoke} from '@tauri-apps/api/core'
import {listen, type UnlistenFn} from '@tauri-apps/api/event'
import {info, error as logError, warn, debug} from '@tauri-apps/plugin-log'
import type {OllamaModel, OllamaModelDetails, OllamaModelInfo, SendPromptParams} from "~/types"

export const useOllamaStore = defineStore('ollama', () => {
  const models = ref<OllamaModel[]>([])
  const currentModelDetails = ref<OllamaModelDetails | null>(null)
  const pullProgress = ref<string>('')
  const error = ref<string | null>(null)
  const isLoading = ref(false)
  const isSending = ref(false)
  const isStreaming = ref(false)
  const currentStreamingMessage = ref('')
  const promptError = ref<string | null>(null)
  const localModels = ref<OllamaModelInfo[]>([])
  
  // Ollama Detection
  const isOllamaInstalled = ref<boolean | null>(null)
  const isChecking = ref(false)
  const checkError = ref<string | null>(null)

  const hasModels = computed(() => models.value.length > 0)

  let unlistenResponse: UnlistenFn | null = null
  let unlistenResponseEnd: UnlistenFn | null = null

  const fetchOllamaModels = async (retries = 3) => {
    isLoading.value = true
    error.value = null
    try {
      models.value = await invoke<OllamaModel[]>('get_ollama_models')
      await info(`Successfully fetched ${models.value.length} Ollama models`)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'An unknown error occurred'
      if (retries > 0) {
        await logError(`Failed to fetch Ollama models: ${errorMessage}. Retrying...`)
        await new Promise(resolve => setTimeout(resolve, 5000)) // Wait for 5 seconds before retrying
        return fetchOllamaModels(retries - 1)
      }
      error.value = errorMessage
      await logError(`Failed to fetch Ollama models after multiple attempts: ${errorMessage}`)
      await logError(`Error details: ${JSON.stringify(err)}`)
    } finally {
      isLoading.value = false
    }
  }

  const fetchModelDetails = async (modelName: string) => {
    try {
      currentModelDetails.value = await invoke<OllamaModelDetails>('show_ollama_model', {modelName})
      error.value = null
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An unknown error occurred'
      currentModelDetails.value = null
    }
  }

  const fetchLocalModels = async (retries = 3) => {
    isLoading.value = true
    error.value = null
    try {
      localModels.value = await invoke<OllamaModelInfo[]>('list_ollama_models')
      await info(`Successfully fetched ${localModels.value.length} local Ollama models`)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'An unknown error occurred'
      if (retries > 0) {
        await logError(`Failed to fetch local Ollama models: ${errorMessage}. Retrying...`)
        await new Promise(resolve => setTimeout(resolve, 5000)) // Wait for 5 seconds before retrying
        return fetchLocalModels(retries - 1)
      }
      error.value = errorMessage
      await logError(`Failed to fetch local Ollama models after multiple attempts: ${errorMessage}`)
      await logError(`Error details: ${JSON.stringify(err)}`)
    } finally {
      isLoading.value = false
    }
  }

  const pullModel = async (modelName: string) => {
    try {
      await invoke('pull_ollama_model', {modelName})
      error.value = null

      await listen<string>('ollama-pull-progress', (event) => {
        pullProgress.value = event.payload
      })

      await listen<string>('ollama-pull-error', (event) => {
        error.value = event.payload
      })
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'An unknown error occurred'
    }
  }

  const setupListeners = async () => {
    unlistenResponse = await listen<string>('ollama-response', (event) => {
      currentStreamingMessage.value += event.payload
      isStreaming.value = true
    })

    unlistenResponseEnd = await listen('ollama-response-end', () => {
      isStreaming.value = false
      // Here you might want to add the message to a chat history if needed
      currentStreamingMessage.value = ''
      info('Ollama response stream ended');
    })
  }

  const removeListeners = () => {
    if (unlistenResponse) unlistenResponse()
    if (unlistenResponseEnd) unlistenResponseEnd()
  }

  const sendPrompt = async ({
                              message,
                              userId,
                              editorContent,
                              language,
                              selectedModel
                            }: SendPromptParams) => {
    if (!message.trim() || !selectedModel) return
    isSending.value = true
    isStreaming.value = true
    promptError.value = null
    currentStreamingMessage.value = ''
    try {
      let fullPrompt: string
      if (editorContent && editorContent.trim()) {
        fullPrompt = `User's code (${language || 'unspecified language'}):\n\`\`\`${language || ''}\n${editorContent}\n\`\`\`\n\nUser's question: ${message}`
      } else {
        fullPrompt = message
      }

      await invoke('send_prompt_to_ollama', {
        model: selectedModel,
        prompt: fullPrompt,
        userId: userId
      })
      await info(`Prompt sent successfully to model: ${selectedModel}`)
    } catch (err) {
      if (err instanceof Error) {
        await logError(`Failed to send prompt to Ollama model ${selectedModel}: ${err.message}`)
        promptError.value = err.message
      } else {
        await logError(`An unknown error occurred while sending prompt to Ollama model ${selectedModel}`)
        promptError.value = 'An unknown error occurred'
      }
    } finally {
      isSending.value = false
    }
  }

  const checkOllamaInstallation = async () => {
    if (isChecking.value || isOllamaInstalled.value !== null) return
    isChecking.value = true
    checkError.value = null
    try {
      const result = await invoke<boolean>('is_ollama_installed')
      isOllamaInstalled.value = result
      if (result) {
        await info('Ollama is installed')
      } else {
        await warn('Ollama is not installed')
      }
    } catch (err) {
      isOllamaInstalled.value = false
      if (err instanceof Error) {
        switch (err.message) {
          case 'Ollama not found':
            await warn('Ollama is not installed. Please install it to continue.')
            checkError.value = 'Ollama not found. Please install it to continue.'
            break
          case 'Unsupported operating system':
            await logError('Your operating system is not supported for Ollama detection.')
            checkError.value = 'Your operating system is not supported for Ollama detection.'
            break
          case 'Failed to execute system command':
            await logError('An error occurred while checking for Ollama installation.')
            checkError.value = 'An error occurred while checking for Ollama installation.'
            break
          default:
            await logError(`An unexpected error occurred: ${err.message}`)
            checkError.value = `An unexpected error occurred: ${err.message}`
        }
      } else {
        await logError('An unknown error occurred')
        checkError.value = 'An unknown error occurred'
      }
    } finally {
      isChecking.value = false
      await debug(`Ollama installation check completed. Installed: ${isOllamaInstalled.value}`)
    }
  }

  return {
    models,
    localModels,
    currentModelDetails,
    pullProgress,
    error,
    isLoading,
    isSending,
    isStreaming,
    currentStreamingMessage,
    promptError,
    hasModels,
    isOllamaInstalled,
    isChecking,
    checkError,
    fetchOllamaModels,
    fetchModelDetails,
    fetchLocalModels,
    pullModel,
    setupListeners,
    removeListeners,
    sendPrompt,
    checkOllamaInstallation
  }
})