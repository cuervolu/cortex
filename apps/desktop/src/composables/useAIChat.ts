import {error as logError} from "@tauri-apps/plugin-log";

import {useToast} from "@cortex/shared/components/ui/toast";
import {createExerciseContext} from "~/types";

export function useAIChat() {
  const isErrorInProgress = ref(false)
  const selectedModel = ref('claude')
  const isChangingProvider = ref(false)
  const apiKeyStatus = reactive({
    isLoading: false,
    error: null as string | null,
  })

  const chatStore = useChatStore()
  const keystore = useKeystore()
  const providerStore = useAIProviderStore()
  const errorHandler = useDesktopErrorHandler()
  const {toast} = useToast()
  const {data: authData} = useAuth()

  const verifyApiKey = async (providerName: string): Promise<boolean> => {
    if (!authData.value?.id) return false

    try {
      apiKeyStatus.isLoading = true
      apiKeyStatus.error = null

      await keystore.initializeKeystore()

      const apiKey = await keystore.getApiKey(providerName)
      const hasKey = !!apiKey

      if (hasKey) {
        await providerStore.setProviderConfigured(providerName, true)
        return true
      }

      apiKeyStatus.error = `No API key configured for ${providerName}`
      await providerStore.setProviderConfigured(providerName, false)
      return false
    } catch (error) {
      apiKeyStatus.error = error instanceof Error ? error.message : 'Failed to verify API key'
      await providerStore.setProviderConfigured(providerName, false)
      return false
    } finally {
      apiKeyStatus.isLoading = false
    }
  }

  const initializeChat = async (exercise: any, editorContent: any) => {
    if (!exercise || !authData.value?.id) return

    try {
      let provider = providerStore.getProvider(selectedModel.value)
      if (!provider) throw new Error('Invalid provider')

      if (provider.requiresApiKey) {
        const hasValidKey = await verifyApiKey(selectedModel.value)
        if (!hasValidKey) {
          selectedModel.value = 'ollama'
          provider = providerStore.getProvider('ollama')
        }
      }

      await chatStore.setProvider(selectedModel.value)

      const context = createExerciseContext({
        exercise_id: exercise.id.toString(),
        exercise_slug: exercise.slug,
        username: authData.value.username || 'anonymous',
        exercise_title: exercise.title,
        exercise_instructions: exercise.instructions,
        exercise_hints: exercise.hints,
        editor_content: editorContent.value,
        editor_language: exercise.language
      })

      await chatStore.startSession(context)
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 401,
        silent: true,
        data: {
          action: 'initialize_chat',
          provider: selectedModel.value
        }
      })
    }
  }


  const handleModelChange = async (model: { value: string, label: string }) => {
    if (!authData.value?.id || isChangingProvider.value) return
    const previousModel = selectedModel.value

    try {
      isChangingProvider.value = true
      const provider = providerStore.getProvider(model.value)
      if (!provider) return

      if (provider.requiresApiKey) {
        const hasKey = await verifyApiKey(model.value)
        if (!hasKey) {
          selectedModel.value = previousModel
          chatStore.clearChat()

          toast({
            title: "API Key Required",
            description: `Please configure API key for ${model.value} in settings`,
            variant: "default",
          })
          return
        }
      }

      selectedModel.value = model.value
      await chatStore.setProvider(model.value)
      chatStore.clearChat()

    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 401,
        silent: true,
        data: {
          action: 'model_change',
          provider: model.value
        }
      })
      selectedModel.value = previousModel
    } finally {
      isChangingProvider.value = false
    }
  }

  return {
    selectedModel,
    isChangingProvider,
    apiKeyStatus,
    chatStore,
    initializeChat,
    handleModelChange,
    verifyApiKey,
    isErrorInProgress
  }
}