<script setup lang="ts">
import { invoke } from '@tauri-apps/api/core';
import { useOllamaStore } from '~/stores/useOllama.store';
import { useChatStore } from '~/stores/useChat.store';
import type { ExerciseDetail } from '~/types';
import AiChat from '@cortex/shared/components/ai/chat.vue';
import CodeEditor from '@cortex/shared/components/CodeEditor.vue';
import ExerciseHeader from '@cortex/shared/components/exercise/ExerciseHeader.vue';
import ExercisePanel from '@cortex/shared/components/exercise/ExercisePanel.vue';
import { materialDark, materialLight } from '@cortex/shared/themes';
import logo from "~/assets/img/Cortex Logo.svg";

const route = useRoute();
const router = useRouter();
const exercise = ref<ExerciseDetail | null>(null);
const ollamaStore = useOllamaStore();
const chatStore = useChatStore();

const currentLesson = ref('');
const currentExercise = computed(() => exercise.value?.title || '');
const currentFileName = ref('exercise.py');
const currentLanguage = ref('python');
const initialCode = ref('');
const editorCode = ref('');

// Configuración del editor
const availableExtensions = [
  'lineNumbersRelative',
  'indentationMarkers',
  'interact',
];
const availableThemes = { materialLight, materialDark };
const activeExtensions = ref([
  'lineNumbersRelative',
  'indentationMarkers',
  'interact',
]);
const colorMode = useColorMode();
const colorModePreference = toRef(colorMode, 'preference');
const activeTheme = ref('');


// Estado del panel
const isPanelOpen = ref(true);

const fetchExerciseDetails = async () => {
  const id = Number(route.params.id);
  try {
    const response = await invoke<ExerciseDetail>('get_exercise_details', {
      id,
    });
    exercise.value = response;
    initialCode.value = response.initial_code;
    editorCode.value = response.initial_code;
    currentFileName.value = response.file_name;
    currentLanguage.value = response.language;
    currentLesson.value = response.lesson_name;
    console.log('Fetched exercise details:', response);
  } catch (error) {
    console.error('Failed to fetch exercise details:', error);
  }
};

const handleSendMessage = async (message: string) => {
  if (!exercise.value) return;
  chatStore.addMessage({ sender: 'user', content: message });
  chatStore.setIsStreaming(true);
  chatStore.clearStreamingMessage();
  await ollamaStore.sendPrompt({
    message,
    userId: 'user-id',
    editorContent: editorCode.value,
    language: currentLanguage.value,
    selectedModel: 'phi3.5:latest',
  });
};

const handleCodeChange = (newCode: string) => {
  editorCode.value = newCode;
};

const handleBackClick = () => {
  router.push('/exercises');
};

const handleSettingsClick = () => {
  isPanelOpen.value = !isPanelOpen.value;
};

const panelTabs = computed(() => [
  {
    value: 'ia-help',
    label: 'AI Help',
    component: AiChat,
    props: {
      messages: chatStore.messages,
      isStreaming: chatStore.isStreaming,
      currentStreamingMessage: chatStore.currentStreamingMessage,
      avatarSrc: logo,
    },
  },
]);
const defaultPanelTab = 'ia-help';

watch(colorModePreference, (newMode) => {
  activeTheme.value = newMode === 'dark' ? 'materialDark' : 'materialLight'; 
});


onMounted(() => {
  fetchExerciseDetails();
  ollamaStore.setupListeners();
});

onUnmounted(() => {
  ollamaStore.removeListeners();
});


watch(initialCode, (newCode) => {
  editorCode.value = newCode;
});


watch(
  () => ollamaStore.isStreaming,
  (newValue) => {
    if (!newValue) {
      chatStore.finishAIMessage();
    }
  }
);
</script>

<template>
  <div class="flex flex-col h-full w-full bg-background">
    <ExerciseHeader
      :lesson="currentLesson"
      :exercise-name="currentExercise"
      :file-name="currentFileName"
      :on-back-click="handleBackClick"
      :on-settings-click="handleSettingsClick"
    />

    <ResizablePanelGroup
      direction="horizontal"
      class="flex flex-grow overflow-hidden"
    >
      <ResizablePanel class="flex-grow p-5 bg-neutral-50">
        <div class="h-full rounded-md overflow-hidden">
          <CodeEditor
            v-if="initialCode"
            v-model:code="editorCode"
            :language="currentLanguage"
            :initial-code="initialCode"
            :available-extensions="availableExtensions"
            :available-themes="availableThemes"
            :active-extensions="activeExtensions"
            :active-theme="activeTheme"
            @change="handleCodeChange"
          />
        </div>
      </ResizablePanel>
      <ResizableHandle  />
      <ResizablePanel>
        <ExercisePanel
          :tabs="panelTabs"
          :default-tab="defaultPanelTab"
          :is-open="isPanelOpen"
          class="w-[50rem] h-full flex-shrink-0 hidden sm:block shadow-lg pl-5 shadow-gray-500/50"
          @send-message="handleSendMessage"
          @update:is-open="isPanelOpen = $event"
      /></ResizablePanel>
    </ResizablePanelGroup>
  </div>
</template>
