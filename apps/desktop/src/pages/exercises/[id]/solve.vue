<script setup lang="ts">
import {debug, error as logError} from "@tauri-apps/plugin-log";

import {useChatStore} from '~/stores';
import {createExerciseContext} from '~/types';
import AiChat from '~/components/ai/chat.vue';
import CodeEditor from '@cortex/shared/components/CodeEditor.vue';
import ModelSelector from '~/components/ai/ModelSelector.vue';
import ExerciseHeader from '@cortex/shared/components/exercise/ExerciseHeader.vue';
import ExercisePanel from '@cortex/shared/components/exercise/ExercisePanel.vue';
import BotIcon from '~/components/icons/BotIcon.vue';
import {useExercise, useCodeEditor, usePanel} from '~/composables';
import LoadingOverlay from "~/components/LoadingOverlay.vue";
import {useToast} from "@cortex/shared/components/ui/toast";

const isErrorInProgress = ref(false);
const {data: authData} = useAuth();
const router = useRouter();
const chatStore = useChatStore();
const keystore = useKeystore();
const {data} = useAuth();
const isMounted = ref(true);
const selectedModel = ref('claude');
const isLoading = ref(true);
const isChangingProvider = ref(false);
const providerStore = useAIProviderStore();
const {handleError} = useErrorHandler();
const { toast } = useToast();
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

const {isPanelOpen} = usePanel();
const {availableExtensions, availableThemes, activeExtensions, editorTheme} = useCodeEditor();
const isSettingsOpen = ref(false)

const handleSettingsClick = () => {
  isSettingsOpen.value = true
}

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

    await providerStore.setProviderConfigured(providerName, true);
    return true;
  } catch (error) {
    apiKeyStatus.error = error instanceof Error ? error.message : 'Failed to verify API key';
    await providerStore.setProviderConfigured(providerName, false);
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
    await handleError(error);
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
    await handleError(error);
  }
};

const handleModelChange = async (model: { value: string, label: string }) => {
  if (!isMounted.value || !authData.value?.id || isChangingProvider.value) return;
  const previousModel = selectedModel.value;
  try {
    isChangingProvider.value = true;
    const previousModel = selectedModel.value;
    
    const provider = providerStore.getProvider(model.value);
    if (!provider) return;

    if (provider.requiresApiKey) {
      const hasKey = await verifyApiKey(model.value);
      if (!hasKey) {
        selectedModel.value = previousModel;
        chatStore.clearChat(); 
        
        toast({
          title: "API Key Required",
          description: `Please configure API key for ${model.value} in settings`,
          variant: "default",
        });
        return;
      }
    }

    selectedModel.value = model.value;
    await chatStore.setProvider(model.value);

    // Solo iniciar sesión si el proveedor está configurado correctamente
    if (!provider.requiresApiKey || (provider.requiresApiKey && provider.isConfigured)) {
      await initializeChat();
    }

    chatStore.clearChat();
  } catch (error) {
    await handleError(error, {
      statusCode: 401,
      silent: true,
      data: {
        action: 'model_change',
        provider: model.value
      }
    });
    selectedModel.value = previousModel;
  } finally {
    isChangingProvider.value = false;
  }
};

const handleBackClick = () => {
  router.push('/exercises');
};

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
    await handleError(error);
  } finally {
    if (isMounted.value) {
      isLoading.value = false;
    }
  }
});

onUnmounted(() => {
  isMounted.value = false;
  isErrorInProgress.value = false;
  chatStore.removeListeners();
});
onBeforeRouteLeave(() => {
  isErrorInProgress.value = false;
  chatStore.removeListeners();
});

watch(initialCode, (newCode) => {
  if (isMounted.value) {
    editorCode.value = newCode;
  }
});

watch(selectedModel, async (newModel) => {
  if (isMounted.value && !isErrorInProgress.value) {
    try {
      isErrorInProgress.value = true;
      await chatStore.setProvider(newModel);
    } catch (error) {
      await debug(`Error changing model: ${error}`);
    } finally {
      isErrorInProgress.value = false;
    }
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

    <SettingsSheet
        v-model="selectedModel"
        v-model:is-open="isSettingsOpen"
        @change="handleModelChange"
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
        <ScrollArea class="flex flex-col h-full">
          <ExercisePanel
              :tabs="panelTabs"
              :default-tab="defaultPanelTab"
              :is-open="isPanelOpen"
              class="flex-1"
              @send-message="handleSendMessage"
              @update:is-open="isPanelOpen = $event"
          />
        </ScrollArea>
      </ResizablePanel>
    </ResizablePanelGroup>
  </div>
</template>