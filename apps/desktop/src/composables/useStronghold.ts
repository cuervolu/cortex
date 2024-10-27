import type { Client } from '@tauri-apps/plugin-stronghold';
import { Stronghold } from '@tauri-apps/plugin-stronghold'
import { info, error as logError } from "@tauri-apps/plugin-log";
import { appDataDir } from '@tauri-apps/api/path'
import { useAnthropicStore } from "~/stores/anthropic.store";

export const useStronghold = () => {
  let strongholdInstance: Stronghold | undefined;
  let clientInstance: Client | undefined;

  const initStronghold = async () => {
    try {
      await info('Initializing Stronghold')
      
      if (strongholdInstance && clientInstance) {
        return {
          stronghold: strongholdInstance,
          client: clientInstance
        }
      }

      const vaultPath = await appDataDir() + 'cortex.hold';
      const vaultPassword = 'cortex_password';

      try {
        strongholdInstance = await Stronghold.load(vaultPath, vaultPassword);
      } catch (e) {
        await logError(`Failed to load existing stronghold, creating new one: ${e}`);
        strongholdInstance = await Stronghold.load(vaultPath, vaultPassword);
        await strongholdInstance.save();
      }

      if (!strongholdInstance) {
        throw new Error('Failed to initialize Stronghold instance');
      }

      const clientName = 'api_keys';

      try {
        clientInstance = await strongholdInstance.loadClient(clientName);
      } catch {
        if (!strongholdInstance) {
          throw new Error('Stronghold instance is null');
        }
        clientInstance = await strongholdInstance.createClient(clientName);
        await strongholdInstance.save();
      }

      if (!clientInstance) {
        throw new Error('Failed to initialize Stronghold client');
      }

      return {
        stronghold: strongholdInstance,
        client: clientInstance
      }
    } catch (error) {
      await logError(`Error initializing Stronghold: ${error}`);
      throw new Error(`Failed to initialize Stronghold: ${error}`);
    }
  }

  const saveApiKey = async (apiKey: string) => {
    try {
      const { stronghold, client } = await initStronghold();

      if (!client || !stronghold) {
        throw new Error('Stronghold not properly initialized');
      }

      const store = client.getStore();

      // Primero intentamos eliminar cualquier clave existente
      try {
        await store.remove('anthropic_api_key');
      } catch {
        // Ignorar error si no existe
      }

      const data = Array.from(new TextEncoder().encode(apiKey));
      await store.insert('anthropic_api_key', data);
      await stronghold.save();

      const apiKeyStore = useAnthropicStore();
      apiKeyStore.setKeyConfigured(true);

      return true;
    } catch (error) {
      await logError(`Error saving API key: ${error}`);
      throw new Error(`Failed to save API key: ${error}`);
    }
  }

  const getApiKey = async (): Promise<string> => {
    try {
      await info('Getting API key');
      const { client } = await initStronghold();

      if (!client) {
        throw new Error('Stronghold client not initialized');
      }

      const store = client.getStore();
      const data = await store.get('anthropic_api_key');

      if (!data) {
        throw new Error('No API key found');
      }

      if (!Array.isArray(data)) {
        throw new Error('Invalid API key format');
      }

      const uint8Array = new Uint8Array(data);
      const decoded = new TextDecoder().decode(uint8Array);

      if (!decoded) {
        throw new Error('Failed to decode API key');
      }

      return decoded;
    } catch (error) {
      await logError(`Error getting API key: ${error}`);
      throw error;
    }
  }

  const removeApiKey = async () => {
    try {
      await info('Removing API key');
      const { stronghold, client } = await initStronghold();

      if (!client || !stronghold) {
        throw new Error('Stronghold not properly initialized');
      }

      const store = client.getStore();
      await store.remove('anthropic_api_key');
      await stronghold.save();

      const apiKeyStore = useAnthropicStore();
      apiKeyStore.setKeyConfigured(false);

      return true;
    } catch (error) {
      await logError(`Error removing API key: ${error}`);
      throw new Error(`Failed to remove API key: ${error}`);
    }
  }

  const isKeyConfigured = async (): Promise<boolean> => {
    try {
      await getApiKey();
      return true;
    } catch {
      return false;
    }
  }

  return {
    saveApiKey,
    getApiKey,
    removeApiKey,
    isKeyConfigured
  }
}