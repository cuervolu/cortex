import { invoke } from '@tauri-apps/api/core'
import { debug, error as logError } from '@tauri-apps/plugin-log'
import {AppError} from "@cortex/shared/types";

interface KeystoreState {
  initialized: boolean
  providers: Set<string>
}

const state: KeystoreState = {
  initialized: false,
  providers: new Set()
}

export const useKeystore = () => {
  const { data: authData } = useAuth()
  const errorHandler = useDesktopErrorHandler()

  const initializeKeystore = async () => {
    if (state.initialized || !authData.value?.id) {
      return
    }

    try {
      await invoke('set_provider_api_key', {
        providerName: 'init',
        apiKey: null,
        userId: authData.value.id
      })
      state.initialized = true
      await debug('Keystore initialized successfully')
    } catch (error) {
      // Silently initialize state but log the error
      state.initialized = true
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: { action: 'initialize_keystore' },
        silent: true
      })
    }
  }

  const getApiKey = async (provider: string): Promise<string | null> => {
    if (!state.initialized) {
      await initializeKeystore()
    }

    try {
      const key = await invoke<string>('get_provider_api_key', {
        providerName: provider
      })
      state.providers.add(provider)
      await debug(`API key retrieved for provider: ${provider}`)
      return key
    } catch (error) {
      // No key found is an expected case, don't treat as error
      if (error instanceof Error && error.message.includes('No API key configured')) {
        await debug(`No API key found for provider: ${provider}`)
        return null
      }

      // For other errors, handle them but keep them silent
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: { action: 'get_api_key', provider },
        silent: true
      })
      return null
    }
  }

  const setApiKey = async (provider: string, key: string) => {
    if (!authData.value?.id) {
      throw new AppError('User authentication required', {
        statusCode: 401,
        data: {
          action: 'set_api_key',
          provider
        }
      })
    }

    if (!state.initialized) {
      await initializeKeystore()
    }

    try {
      await invoke('set_provider_api_key', {
        providerName: provider,
        apiKey: key,
        userId: authData.value.id
      })
      
      state.providers.add(provider)
      const providerStore = useAIProviderStore()
      await providerStore.setProviderConfigured(provider, true)
      
      await debug(`API key set successfully for provider: ${provider}`)
      
      return true
    } catch (error) {
      await logError(`Failed to set API key for ${provider}: ${error}`)
      const providerStore = useAIProviderStore()
      await providerStore.setProviderConfigured(provider, false)
      
      throw new AppError(
        `Failed to set API key for ${provider}`,
        {
          statusCode: 500,
          data: {
            action: 'set_api_key',
            provider,
            userId: authData.value.id
          }
        }
      )
    }
  }

  const removeProviderKey = async (provider: string) => {
    if (!state.initialized) {
      await initializeKeystore()
    }

    try {
      await invoke('remove_provider_api_key', {
        providerName: provider
      })
      state.providers.delete(provider)
      await debug(`API key removed for provider: ${provider}`)
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: {
          action: 'remove_provider_key',
          provider
        },
        silent: true
      })
    }
  }

  const removeAllKeys = async () => {
    if (!state.initialized) {
      await initializeKeystore()
    }

    try {
      await invoke('remove_all_api_keys')
      state.providers.clear()
      await debug('All API keys removed successfully')
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: { action: 'remove_all_keys' }
      })
    }
  }

  const resetKeystore = () => {
    state.initialized = false
    state.providers.clear()
    debug('Keystore state reset')
  }

  return {
    initializeKeystore,
    getApiKey,
    setApiKey,
    removeProviderKey,
    removeAllKeys,
    resetKeystore,
    isInitialized: () => state.initialized,
    hasProviderKey: (provider: string) => state.providers.has(provider)
  }
}