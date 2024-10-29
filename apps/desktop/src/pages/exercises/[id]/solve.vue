<script setup lang="ts">
import {error as logError} from "@tauri-apps/plugin-log";

import { useChatStore } from '~/stores';
import { createExerciseContext } from '~/types';
import AiChat from '~/components/ai/chat.vue';
import CodeEditor from '@cortex/shared/components/CodeEditor.vue';
import ModelSelector from '~/components/ai/ModelSelector.vue';
import ExerciseHeader from '@cortex/shared/components/exercise/ExerciseHeader.vue';
import ExercisePanel from '@cortex/shared/components/exercise/ExercisePanel.vue';
import BotIcon from '~/components/icons/BotIcon.vue';
import { useExercise, useCodeEditor, usePanel } from '~/composables';
import LoadingOverlay from "~/components/LoadingOverlay.vue";


const { data: authData } = useAuth();
const router = useRouter();
const chatStore = useChatStore();
const keystore = useKeystore();
const { data } = useAuth();
const isMounted = ref(true);
const selectedModel = ref('claude');
const isLoading = ref(true);
const providerStore = useAIProviderStore();
const { handleError } = useErrorHandler();
const apiKeyStatus = reactive({
  isLoading: false,
  error: null as string | null,
});

const {
  exercise,
  currentLesson,
  currentExercise,
  currentFileName,
  currentLanguage,
  initialCode,
  editorCode,
  fetchExerciseDetails,
  handleCodeChange,
} = useExercise();

const { isPanelOpen, handleSettingsClick } = usePanel();
const { availableExtensions, availableThemes, activeExtensions, editorTheme } = useCodeEditor();







const verifyApiKey = async (providerName: string): Promise<boolean> => {
  if (!authData.value?.id) return false;

  try {
    apiKeyStatus.isLoading = true;
    apiKeyStatus.error = null;

    await keystore.initializeKeystore();
    const apiKey = await keystore.getApiKey(providerName);

    if (!apiKey) {
      apiKeyStatus.error = `No API key configured for ${providerName}`;
      return false;
    }
    
    providerStore.setProviderConfigured(providerName, true);
    return true;
  } catch (error) {
    apiKeyStatus.error = error instanceof Error ? error.message : 'Failed to verify API key';
    providerStore.setProviderConfigured(providerName, false);
    return false;
  } finally {
    apiKeyStatus.isLoading = false;
  }
};

const initializeChat = async () => {
  if (!exercise.value || !authData.value?.id) return;

  try {
    const provider = providerStore.getProvider(selectedModel.value);
    if (!provider) throw new Error('Invalid provider');
    
    await chatStore.setProvider(selectedModel.value);
    
    if (provider.requiresApiKey) {
      const hasValidKey = await verifyApiKey(selectedModel.value);
      if (!hasValidKey) {
        selectedModel.value = 'ollama';
        throw new Error(`${provider.name} requires API key configuration`);
      }
    }

    const context = createExerciseContext({
      exercise_id: exercise.value.id.toString(),
      exercise_slug: exercise.value.slug,
      username: authData.value.username || 'anonymous',
      exercise_title: exercise.value.title,
      exercise_instructions: exercise.value.instructions,
      exercise_hints: exercise.value.hints,
      editor_content: editorCode.value,
      editor_language: currentLanguage.value
    });

    await chatStore.startSession(context);
  } catch (error) {
    await logError(`Error initializing chat: ${error}`);
    handleError(error);
  }
};
const handleSendMessage = async (message: string) => {
  if (!exercise.value || !isMounted.value) return;

  try {
    const context = createExerciseContext({
      exercise_id: exercise.value.id.toString(),
      exercise_slug: exercise.value.slug,
      username: data.value?.username || 'anonymous',
      exercise_title: exercise.value.title,
      exercise_instructions: exercise.value.instructions,
      exercise_hints: exercise.value.hints,
      editor_content: editorCode.value,
      editor_language: currentLanguage.value
    });
    
    if (chatStore.messages.length === 0) {
      await initializeChat();
    }

    await chatStore.sendMessage(context.exercise_id, message);
  } catch (error) {
    if (!isMounted.value) return;
    handleError(error);
  }
};

const handleModelChange = async (model: { value: string, label: string }) => {
  if (!isMounted.value || !authData.value?.id) return;

  try {
    const provider = providerStore.getProvider(model.value);
    if (!provider) throw new Error('Invalid provider');
    
    await chatStore.setProvider(model.value);
    
    if (provider.requiresApiKey) {
      const hasValidKey = await verifyApiKey(model.value);
      if (!hasValidKey) {
        throw new Error(`${model.value} requires API key configuration`);
      }
    }

    selectedModel.value = model.value;
    await initializeChat();
    chatStore.clearChat();
  } catch (error) {
    await logError(`Error changing model: ${error}`);
    handleError(error);
  }
};

// Navegación
const handleBackClick = () => {
  router.push('/exercises');
};

// Computed properties para el estado del chat
const panelTabs = computed(() => [{
  value: 'ia-help',
  label: 'AI Help',
  component: markRaw(AiChat),
  iconSrc: BotIcon,
  props: {
    messages: chatStore.messages,
    isStreaming: chatStore.isStreaming,
    currentStreamingMessage: chatStore.currentStreamingMessage,
    avatarSrc: data.value?.avatar_url || "https://placewaifu.com/image",
    isSending: chatStore.isSending,
  },
}]);

const defaultPanelTab = 'ia-help';

onMounted(async () => {
  if (!isMounted.value || !authData.value?.id) return;

  isLoading.value = true;
  try {
    // Verificar API key al montar
    const provider = providerStore.getProvider(selectedModel.value);
    if (provider?.requiresApiKey) {
      const hasValidKey = await verifyApiKey(selectedModel.value);
      if (!hasValidKey) {
        selectedModel.value = 'ollama';
      }
    }

    await fetchExerciseDetails();
    await initializeChat();
  } catch (error) {
    await logError(`Error during component mount: ${error}`);
    handleError(error);
  } finally {
    if (isMounted.value) {
      isLoading.value = false;
    }
  }
});

onUnmounted(() => {
  isMounted.value = false;
  chatStore.removeListeners();
});

onBeforeRouteLeave(() => {
  chatStore.removeListeners();
});

watch(initialCode, (newCode) => {
  if (isMounted.value) {
    editorCode.value = newCode;
  }
});

watch(selectedModel, async (newModel) => {
  if (isMounted.value) {
    await chatStore.setProvider(newModel);
  }
});
</script>

<template>
  <loading-overlay v-if="isLoading"/>
  <div v-else class="flex flex-col h-full w-full bg-background">
    <ExerciseHeader
        :lesson="currentLesson"
        :exercise-name="currentExercise"
        :file-name="currentFileName"
        :on-back-click="handleBackClick"
        :on-settings-click="handleSettingsClick"
    />
    <ResizablePanelGroup direction="horizontal" class="flex-grow">
      <ResizablePanel :default-size="70" :min-size="30">
        <div class="h-full p-4">
          <CodeEditor
              v-if="initialCode"
              v-model:code="editorCode"
              :language="currentLanguage"
              :initial-code="initialCode"
              :available-extensions="availableExtensions"
              :available-themes="availableThemes"
              :active-extensions="activeExtensions"
              :active-theme="editorTheme"
              class="h-full rounded-md overflow-hidden"
              @change="handleCodeChange"
          />
        </div>
      </ResizablePanel>
      <ResizableHandle/>
      <ResizablePanel :default-size="30" :min-size="20">
        <div class="flex flex-col h-full">
          <ModelSelector
              v-model="selectedModel"
              @change="handleModelChange"
          />
          <ExercisePanel
              :tabs="panelTabs"
              :default-tab="defaultPanelTab"
              :is-open="isPanelOpen"
              class="flex-1"
              @send-message="handleSendMessage"
              @update:is-open="isPanelOpen = $event"
          />
        </div>
      </ResizablePanel>
    </ResizablePanelGroup>
  </div>
</template>