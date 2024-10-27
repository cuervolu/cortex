<script setup lang="ts">
import { ref, markRaw, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAnthropicStore, useOllamaStore, useChatStore } from '~/stores';
import AiChat from '@cortex/shared/components/ai/chat.vue';
import CodeEditor from '@cortex/shared/components/CodeEditor.vue';
import ModelSelector from '~/components/ai/ModelSelector.vue';
import ExerciseHeader from '@cortex/shared/components/exercise/ExerciseHeader.vue';
import ExercisePanel from '@cortex/shared/components/exercise/ExercisePanel.vue';
import BotIcon from '~/components/icons/BotIcon.vue';
import { useExercise, useCodeEditor, usePanel } from '~/composables';
import LoadingOverlay from "~/components/LoadingOverlay.vue";

const router = useRouter();
const ollamaStore = useOllamaStore();
const anthropicStore = useAnthropicStore();
const chatStore = useChatStore();
const { data } = useAuth();

const selectedModel = ref('llama3:8b');
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

const handleModelChange = async (model: { value: string, label: string }) => {
  selectedModel.value = model.value;
  chatStore.clearChat();
  chatStore.addMessage({
    sender: 'ai',
    content: `¡Hola! Ahora estás usando ${model.label}. ¿En qué puedo ayudarte?`
  });
};

const handleSendMessage = async (message: string) => {
  if (!exercise.value) return;

  try {
    chatStore.addMessage({
      sender: 'user',
      content: message
    });

    if (selectedModel.value.startsWith('claude')) {
      const context = {
        exerciseTitle: exercise.value.title,
        exerciseInstructions: exercise.value.instructions,
        exerciseHints: exercise.value.hints,
        editorContent: editorCode.value,
        editorLanguage: currentLanguage.value
      };

      if (chatStore.messages.length > 1) {
        await anthropicStore.sendMessageWithHistory(message, context);
      } else {
        await anthropicStore.sendMessage(message, context);
      }
    } else {
      await ollamaStore.sendPrompt({
        message,
        exerciseSlug: exercise.value.slug,
        editorContent: editorCode.value,
        language: currentLanguage.value,
      });
    }
  } catch (error) {
    console.error('Error sending message:', error);
    chatStore.setPromptError(error instanceof Error ? error.message : 'Error desconocido');
  }
};

const handleBackClick = () => {
  router.push('/exercises');
};

const isSending = computed(() =>
    selectedModel.value.startsWith('claude')
        ? anthropicStore.isLoading
        : ollamaStore.isSending
);

const panelTabs = computed(() => [
  {
    value: 'ia-help',
    label: 'AI Help',
    component: markRaw(AiChat),
    iconSrc: BotIcon,
    props: {
      messages: chatStore.messages,
      isStreaming: chatStore.isStreaming,
      currentStreamingMessage: chatStore.currentStreamingMessage,
      avatarSrc: data.value?.avatar_url || "https://placewaifu.com/image",
      isSending: isSending.value,
    },
  },
]);

const defaultPanelTab = 'ia-help';
const isLoading = ref(true);

onMounted(async () => {
  await fetchExerciseDetails();
  // Setup listeners según el modelo seleccionado
  if (!selectedModel.value.startsWith('claude')) {
    await ollamaStore.setupListeners();
  }
  isLoading.value = false;
});

onUnmounted(() => {
  if (!selectedModel.value.startsWith('claude')) {
    ollamaStore.removeListeners();
  }
});

watch(initialCode, (newCode) => {
  editorCode.value = newCode;
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