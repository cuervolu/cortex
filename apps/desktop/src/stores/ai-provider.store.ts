import {error as logError} from "@tauri-apps/plugin-log";
import type { AIProvider } from '~/types'

export const useAIProviderStore = defineStore('ai-provider', () => {
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

  // Verifica y actualiza el estado de configuración del proveedor
  const checkProviderConfiguration = async (providerName: string) => {
    const provider = providers[providerName];
    if (!provider) return;

    if (provider.requiresApiKey) {
      const keystore = useKeystore();
      try {
        if (!keystore.isInitialized()) {
          await keystore.initializeKeystore();
        }
        // Solo verificamos si existe la key
        await keystore.getApiKey(providerName);
        provider.isConfigured = true;
      } catch (error) {
        await logError(`Error checking provider configuration: ${error}`);
        provider.isConfigured = false;
      }
    }
  }

  // Actualiza el estado de configuración
  const setProviderConfigured = (providerName: string, isConfigured: boolean) => {
    if (providers[providerName]) {
      providers[providerName].isConfigured = isConfigured;
    }
  }

  const getProvider = (name: string): AIProvider | undefined => {
    return providers[name];
  }

  return {
    providers: computed(() => providers),
    getProvider,
    checkProviderConfiguration,
    setProviderConfigured
  }
})