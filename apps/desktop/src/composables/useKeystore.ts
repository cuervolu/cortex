import { invoke } from '@tauri-apps/api/core';
import { debug, error as logError } from '@tauri-apps/plugin-log';

// Variable para mantener el estado de inicialización
let isInitialized = false;

export const useKeystore = () => {
  const { data: authData } = useAuth();

  const initializeKeystore = async () => {
    if (isInitialized) {
      await debug('Keystore already initialized');
      return;
    }

    if (!authData.value?.id) {
      throw new Error('User not authenticated');
    }

    try {
      await debug('Initializing keystore');
      await invoke('set_provider_api_key', {
        providerName: 'init',
        apiKey: null,
        userId: authData.value.id
      });
      isInitialized = true;
      await debug('Keystore initialized successfully');
    } catch (error) {
      // Si el error es "No API key configured", consideramos que la inicialización fue exitosa
      if (String(error).includes('No API key configured')) {
        isInitialized = true;
        await debug('Keystore initialized with default state');
        return;
      }
      await logError(`Error initializing keystore: ${error}`);
      isInitialized = false;
      throw error;
    }
  };

  const getApiKey = async (provider: string) => {
    try {
      if (!isInitialized) {
        await initializeKeystore();
      }
      return await invoke<string>('get_provider_api_key', {
        providerName: provider
      });
    } catch (error) {
      if (String(error).includes('No API key configured')) {
        await debug(`No API key configured for provider ${provider}`);
        return null;
      }
      await logError(`Error getting API key: ${error}`);
      throw error;
    }
  };

  const setApiKey = async (provider: string, key: string) => {
    try {
      if (!authData.value?.id) {
        throw new Error('User not authenticated');
      }

      if (!isInitialized) {
        await initializeKeystore();
      }
      
      await invoke('set_provider_api_key', {
        providerName: provider,
        apiKey: key,
        userId: authData.value.id
      });
      await debug('API key set successfully');
    } catch (error) {
      await logError(`Error setting API key: ${error}`);
      throw error;
    }
  };

  const removeApiKey = async () => {
    try {
      if (!isInitialized) {
        await initializeKeystore();
      }
      await invoke('remove_provider_api_key');
      await debug('API key removed successfully');
    } catch (error) {
      // Si no hay clave para eliminar, no consideramos que sea un error
      if (String(error).includes('No API key to remove')) {
        await debug('No API key to remove');
        return;
      }
      await logError(`Error removing API key: ${error}`);
      throw error;
    }
  };

  const resetKeystore = () => {
    isInitialized = false;
  };

  return {
    initializeKeystore,
    getApiKey,
    setApiKey,
    removeApiKey,
    resetKeystore,
    isInitialized: () => isInitialized
  };
};