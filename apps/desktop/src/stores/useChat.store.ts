import {invoke} from '@tauri-apps/api/core'
import {listen} from '@tauri-apps/api/event'
import type {ChatState, ExerciseContext} from '~/types'
import {debug, info} from "@tauri-apps/plugin-log";

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

  let unlistenStream: (() => void) | null = null
  let unlistenStreamEnd: (() => void) | null = null
  let lastChunk: string = ''

  const setupListeners = async (providerName: string) => {
    removeListeners();
    lastChunk = '';

    const streamEvent = `${providerName}-stream`;
    const streamEndEvent = `${providerName}-stream-end`;

    unlistenStream = await listen<string>(streamEvent, (event) => {
      state.isStreaming = true;
      const chunk = event.payload;

      if (chunk !== lastChunk) {
        state.currentStreamingMessage += chunk;
        lastChunk = chunk;
      }
    });

    unlistenStreamEnd = await listen(streamEndEvent, () => {
      if (state.currentStreamingMessage) {
        state.messages.push({
          sender: 'ai',
          content: state.currentStreamingMessage,
          timestamp: new Date().toISOString()
        });
      }
      state.currentStreamingMessage = '';
      state.isStreaming = false;
      state.isSending = false;
      lastChunk = '';
    });

    await info(`Set up listeners for ${providerName}: ${streamEvent}, ${streamEndEvent}`);
  };


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
    try {
      if (!providerName) {
        throw new Error('Invalid provider name');
      }

      const provider = aiProviderStore.getProvider(providerName);
      if (!provider) {
        throw new Error(`Unknown provider: ${providerName}`);
      }

      await debug(`Setting up provider: ${providerName}`);

      // Si el proveedor requiere API key, verificamos su configuración
      if (provider.requiresApiKey) {
        try {
          // Verificar y obtener la API key
          await aiProviderStore.checkProviderConfiguration(providerName);
          if (!provider.isConfigured) {
            throw new Error('Provider not configured');
          }
          await invoke('set_provider', {providerName: providerName});

        } catch (error) {
          await debug(`Error configuring provider: ${error}`);
          aiProviderStore.setProviderConfigured(providerName, false);
          throw new Error('Provider requires API key configuration');
        }
      } else {
        // Para proveedores que no requieren API key
        await invoke('set_provider', {providerName: providerName});
      }

      state.provider.name = providerName;
      state.provider.requiresApiKey = provider.requiresApiKey;
      await setupListeners(providerName);

      await debug(`Provider ${providerName} set up successfully`);

    } catch (error) {
      state.error = error instanceof Error ? error.message : 'Failed to set provider';
      throw error;
    }
  };

  const sendMessage = async (exerciseId: string, content: string) => {
    try {
      state.isSending = true
      state.isStreaming = true
      state.error = null
      state.currentStreamingMessage = '' // Clear last streaming message

      // Add user message to chat
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
      state.error = error instanceof Error ? error.message : 'Failed to send message'
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
      state.error = error instanceof Error ? error.message : 'Failed to start session'
      throw error
    }
  }

  const endSession = async (exerciseId: string) => {
    try {
      await invoke('end_exercise_session', {exerciseId})
      state.messages = []
      state.currentStreamingMessage = ''
      removeListeners()
    } catch (error) {
      state.error = error instanceof Error ? error.message : 'Failed to end session'
      throw error
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