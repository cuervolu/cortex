import { debug } from '@tauri-apps/plugin-log'
import type { AIProvider } from '~/types'
import { AppError } from '@cortex/shared/types'

export const useAIProviderStore = defineStore('ai-provider', () => {
  const errorHandler = useDesktopErrorHandler()

  const providers = reactive<Record<string, AIProvider>>({
    claude: {
      name: 'claude',
      requiresApiKey: true,
      isConfigured: false
    },
    gemini: {
      name: 'gemini',
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
      throw new AppError(`Provider ${providerName} not found`, {
        statusCode: 404,
        data: { providerName }
      })
    }

    if (provider.requiresApiKey) {
      const keystore = useKeystore()
      try {
        if (!keystore.isInitialized()) {
          await keystore.initializeKeystore()
        }

        const hasKey = keystore.hasProviderKey(providerName)
        provider.isConfigured = hasKey
        await debug(`Provider ${providerName} configuration checked - configured: ${hasKey}`)

      } catch (error) {
        provider.isConfigured = false
        await errorHandler.handleError(error, {
          statusCode: 401,
          data: {
            action: 'check_provider_configuration',
            provider: providerName,
            requiresApiKey: provider.requiresApiKey
          },
          silent: true // Most configuration checks should be silent
        })
      }
    }
  }

  const setProviderConfigured = async (providerName: string, isConfigured: boolean) => {
    const provider = providers[providerName]
    if (!provider) {
      throw new AppError(`Cannot configure unknown provider: ${providerName}`, {
        statusCode: 404,
        data: { providerName }
      })
    }

    try {
      provider.isConfigured = isConfigured
      await debug(`Provider ${providerName} configuration status set to: ${isConfigured}`)
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: {
          action: 'set_provider_configured',
          provider: providerName,
          targetStatus: isConfigured
        },
        silent: true
      })
    }
  }

  const getProvider = (name: string): AIProvider | undefined => {
    const provider = providers[name]
    if (!provider) {
      throw new AppError(`Provider ${name} not found`, {
        statusCode: 404,
        data: {
          requestedProvider: name,
          availableProviders: Object.keys(providers)
        }
      })
    }
    return provider
  }

  const checkAllProviders = async () => {
    await debug('Checking configuration for all providers')
    for (const providerName of Object.keys(providers)) {
      if (providers[providerName].requiresApiKey) {
        await checkProviderConfiguration(providerName)
      }
    }
  }

  return {
    providers: computed(() => providers),
    getProvider,
    checkProviderConfiguration,
    checkAllProviders,
    setProviderConfigured
  }
})