import {debug} from "@tauri-apps/plugin-log"
import type {AIProvider} from '~/types'

export const useAIProviderStore = defineStore('ai-provider', () => {
  const {handleError, createAppError} = useErrorHandler()

  const providers = reactive<Record<string, AIProvider>>({
    claude: {
      name: 'claude',
      requiresApiKey: true,
      isConfigured: false
    },
    ollama: {
      name: 'ollama',
      requiresApiKey: false,
      isConfigured: true
    }
  })

  const checkProviderConfiguration = async (providerName: string) => {
    const provider = providers[providerName]
    if (!provider) {
      throw createAppError(`Provider ${providerName} not found`, {
        statusCode: 404,
        data: {providerName}
      })
    }

    if (provider.requiresApiKey) {
      const keystore = useKeystore()
      try {
        if (!keystore.isInitialized()) {
          await keystore.initializeKeystore()
        }

        await keystore.getApiKey(providerName)
        provider.isConfigured = true
        await debug(`Provider ${providerName} configured successfully`)

      } catch (error) {
        provider.isConfigured = false
        await handleError(error, {
          statusCode: 401,
          data: {
            provider: providerName,
            requiresApiKey: provider.requiresApiKey
          },
          cause: error
        })
      }
    }
  }

  const setProviderConfigured = async (providerName: string, isConfigured: boolean) => {
    try {
      if (!providers[providerName]) {
        throw createAppError(`Cannot configure unknown provider: ${providerName}`, {
          statusCode: 404,
          data: {providerName}
        })
      }

      providers[providerName].isConfigured = isConfigured
      await debug(`Provider ${providerName} configuration status set to: ${isConfigured}`)

    } catch (error) {
      await handleError(error, {
        statusCode: 500,
        data: {
          provider: providerName,
          targetStatus: isConfigured
        }
      })
    }
  }

  const getProvider = (name: string): AIProvider | undefined => {
    try {
      const provider = providers[name]
      if (!provider) {
        throw createAppError(`Provider ${name} not found`, {
          statusCode: 404,
          data: {requestedProvider: name, availableProviders: Object.keys(providers)}
        })
      }
      return provider

    } catch (error) {
      handleError(error, {
        statusCode: 404,
        data: {requestedProvider: name}
      })
    }
  }

  return {
    providers: computed(() => providers),
    getProvider,
    checkProviderConfiguration,
    setProviderConfigured
  }
})