import {invoke} from '@tauri-apps/api/core';

interface KeystoreState {
  initialized: boolean;
  hasKey: boolean;
}

const state: KeystoreState = {
  initialized: false,
  hasKey: false
};

export const useKeystore = () => {
  const {data: authData} = useAuth();
  const errorHandler = useDesktopErrorHandler()

  const initializeKeystore = async () => {
    if (state.initialized) {
      return;
    }

    if (!authData.value?.id) {
      return;
    }

    try {
      await invoke('set_provider_api_key', {
        providerName: 'init',
        apiKey: null,
        userId: authData.value.id
      });
      state.initialized = true;
    } catch {
      // Silenciosamente inicializamos el estado
      state.initialized = true;
    }
  };

  const getApiKey = async (provider: string): Promise<string | null> => {
    if (!state.initialized) {
      await initializeKeystore();
    }

    try {
      const key = await invoke<string>('get_provider_api_key', {
        providerName: provider
      });
      state.hasKey = true;
      return key;
    } catch {
      // Si no hay key, simplemente retornamos null sin error
      return null;
    }
  };

  const setApiKey = async (provider: string, key: string) => {
    if (!authData.value?.id) {
      throw new AppError('User not authenticated', {
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
      state.hasKey = true
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: 500,
        data: {
          action: 'set_api_key',
          provider,
          userId: authData.value?.id
        }
      })
    }
  }

  const removeApiKey = async () => {
    if (!state.initialized) {
      await initializeKeystore();
    }

    try {
      await invoke('remove_provider_api_key');
      state.hasKey = false;
    } catch {
      // Si no hay key que remover, simplemente lo ignoramos
      state.hasKey = false;
    }
  };

  const resetKeystore = () => {
    state.initialized = false;
    state.hasKey = false;
  };

  return {
    initializeKeystore,
    getApiKey,
    setApiKey,
    removeApiKey,
    resetKeystore,
    isInitialized: () => state.initialized,
    hasApiKey: () => state.hasKey
  };
};