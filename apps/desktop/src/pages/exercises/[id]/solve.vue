<script setup lang="ts">
import { debug, error as logError } from "@tauri-apps/plugin-log"
import { createExerciseContext } from '~/types'
import AiChat from '~/components/ai/chat.vue'
import CodeEditor from '@cortex/shared/components/CodeEditor.vue'
import ExerciseHeader from '@cortex/shared/components/exercise/ExerciseHeader.vue'
import ExercisePanel from '@cortex/shared/components/exercise/ExercisePanel.vue'
import BotIcon from '~/components/icons/BotIcon.vue'
import LoadingOverlay from "~/components/LoadingOverlay.vue"
import InstructionsTab from "~/components/exercise/InstructionsTab.vue";
import {Book, Lightbulb} from "lucide-vue-next";
import HintsTab from "~/components/exercise/HintsTab.vue";

const {
  isSettingsOpen,
  isLoading,
  isMounted,
  handleSettingsClick,
  handleBackClick
} = useExerciseUI()

const {
  selectedModel,
  chatStore,
  initializeChat,
  handleModelChange,
  isErrorInProgress
} = useAIChat()

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
} = useExercise()

const { isPanelOpen } = usePanel()
const { availableExtensions, availableThemes, activeExtensions, editorTheme } = useCodeEditor()
const { data: authData } = useAuth()

const handleSendMessage = async (message: string) => {
  if (!exercise.value || !isMounted.value) return

  try {
    const context = createExerciseContext({
      exercise_id: exercise.value.id.toString(),
      exercise_slug: exercise.value.slug,
      username: authData.value?.username || 'anonymous',
      exercise_title: exercise.value.title,
      exercise_instructions: exercise.value.instructions,
      exercise_hints: exercise.value.hints,
      editor_content: editorCode.value,
      editor_language: currentLanguage.value
    })

    if (chatStore.messages.length === 0) {
      await initializeChat(exercise.value, editorCode)
    }

    await chatStore.sendMessage(context.exercise_id, message)
  } catch (error) {
    if (!isMounted.value) return
    const { handleError } = useErrorHandler()
    await handleError(error)
  }
}

// Computed properties
const panelTabs = computed(() => [
  {
    value: 'instructions',
    label: 'Instrucciones',
    component: markRaw(InstructionsTab),
    iconSrc: markRaw(Book),
    props: {
      exercise: exercise.value
    },
  },
  {
    value: 'hints',
    label: 'Pistas',
    component: markRaw(HintsTab),
    iconSrc: markRaw(Lightbulb),
    props: {
      exercise: exercise.value
    },
  },
  {
    value: 'ia-help',
    label: 'AI Help',
    component: markRaw(AiChat),
    iconSrc: BotIcon,
    props: {
      messages: chatStore.messages,
      isStreaming: chatStore.isStreaming,
      currentStreamingMessage: chatStore.currentStreamingMessage,
      avatarSrc: authData.value?.avatar_url || "https://placewaifu.com/image",
      isSending: chatStore.isSending,
    },
  }
])

// Cambiamos el tab por defecto a instructions
const defaultPanelTab = 'instructions'

// Lifecycle hooks
onMounted(async () => {
  if (!isMounted.value || !authData.value?.id) return
  isLoading.value = true

  try {
    await fetchExerciseDetails()
    await initializeChat(exercise.value, editorCode)
  } catch (error) {
    await logError(`Error during component mount: ${error}`)
    const { handleError } = useErrorHandler()
    await handleError(error)
  } finally {
    if (isMounted.value) {
      isLoading.value = false
    }
  }
})

onUnmounted(() => {
  isMounted.value = false
  isErrorInProgress.value = false
  chatStore.removeListeners()
})

onBeforeRouteLeave(() => {
  isErrorInProgress.value = false
  chatStore.removeListeners()
})

// Watchers
watch(initialCode, (newCode) => {
  if (isMounted.value) {
    editorCode.value = newCode
  }
})

watch(selectedModel, async (newModel) => {
  if (isMounted.value && !isErrorInProgress.value) {
    try {
      isErrorInProgress.value = true
      await chatStore.setProvider(newModel)
    } catch (error) {
      await debug(`Error changing model: ${error}`)
    } finally {
      isErrorInProgress.value = false
    }
  }
})
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