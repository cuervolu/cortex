<script setup lang="ts">
import { ref, markRaw, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useOllamaStore } from '~/stores/useOllama.store';
import { useChatStore } from '~/stores/useChat.store';
import AiChat from '@cortex/shared/components/ai/chat.vue';
import CodeEditor from '@cortex/shared/components/CodeEditor.vue';
import ExerciseHeader from '@cortex/shared/components/exercise/ExerciseHeader.vue';
import ExercisePanel from '@cortex/shared/components/exercise/ExercisePanel.vue';
import BotIcon from '~/components/icons/BotIcon.vue';
import { useExercise, useCodeEditor, usePanel } from '~/composables';
import LoadingOverlay from "~/components/LoadingOverlay.vue";

const router = useRouter();
const ollamaStore = useOllamaStore();
const chatStore = useChatStore();
const {data} = useAuth();

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

const handleSendMessage = async (message: string) => {
  if (!exercise.value) return;

  await ollamaStore.sendPrompt({
    message,
    exerciseSlug: exercise.value.slug,
    editorContent: editorCode.value,
    language: currentLanguage.value,
  });
};

const handleBackClick = () => {
  router.push('/exercises');
};

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
      isSending: ollamaStore.isSending,
      exerciseSlug: exercise.value?.slug || '',
    },
  },
]);

const defaultPanelTab = 'ia-help';
const isLoading = ref(true);

onMounted(async () => {
  await fetchExerciseDetails();
  await ollamaStore.setupListeners();
  isLoading.value = false;
});

onUnmounted(() => {
  ollamaStore.removeListeners();
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
        <ExercisePanel
            :tabs="panelTabs"
            :default-tab="defaultPanelTab"
            :is-open="isPanelOpen"
            class="h-full"
            @send-message="handleSendMessage"
            @update:is-open="isPanelOpen = $event"
        />
      </ResizablePanel>
    </ResizablePanelGroup>
  </div>
</template>