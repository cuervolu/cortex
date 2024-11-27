<script setup lang="ts">
import {Book, Lightbulb, Activity,MessageCircle} from "lucide-vue-next";

import {useCodeEditor} from '@cortex/shared/composables/useCodeEditor'
import CodeEditor from '@cortex/shared/components/CodeEditor.vue'
import ResultsTab from '@cortex/shared/components/exercise/ResultsTab.vue'
import ExerciseHeader from '@cortex/shared/components/exercise/ExerciseHeader.vue'
import ExercisePanel from '@cortex/shared/components/exercise/ExercisePanel.vue'
import {useCodeExecutionStore} from '@cortex/shared/stores/useCodeExecutionStore';
import {useCodeExecution} from '@cortex/shared/composables/useCodeExecution';
import LoadingOverlay from "@cortex/shared/components/LoadingOverlay.vue"
import InstructionsTab from "@cortex/shared/components/exercise/InstructionsTab.vue";
import HintsTab from "@cortex/shared/components/exercise/HintsTab.vue";
import {AppError} from "@cortex/shared/types";
import {useExercise} from "~/composables/useExercise";
import {useExerciseUI} from "@cortex/shared/composables/useExerciseUI";
import {usePanel} from "@cortex/shared/composables/usePanel";
import {WebCodeExecutionService} from "~/services/web-code-execution.service";
import ChatTab from "@cortex/shared/components/exercise/ChatTab.vue";

const {
  isLoading,
  isMounted,
  handleSettingsClick,
  handleBackClick
} = useExerciseUI()

definePageMeta({
  layout: false,
  auth: true
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
} = useExercise()


const {isPanelOpen} = usePanel()
const {availableExtensions, availableThemes, activeExtensions, editorTheme} = useCodeEditor()
const {data: authData} = useAuth()
const codeExecutionStore = useCodeExecutionStore();

const errorHandler = useWebErrorHandler()

const {executeCode} = useCodeExecution(new WebCodeExecutionService(), {
  maxAttempts: 20,
});
const {$markdown} = useNuxtApp();
// Computed properties
const panelTabs = computed(() => [
  {
    value: 'instructions',
    label: 'Instrucciones',
    component: markRaw(InstructionsTab),
    iconSrc: markRaw(Book),
    props: {
      exercise: exercise.value,
      options: $markdown.options
    },
  },
  {
    value: 'hints',
    label: 'Pistas',
    component: markRaw(HintsTab),
    iconSrc: markRaw(Lightbulb),
    props: {
      exercise: exercise.value,
      options: $markdown.options
    },
  },
  {
    value: 'results',
    label: 'Resultados',
    component: markRaw(ResultsTab),
    iconSrc: markRaw(Activity),
    props: {
      result: codeExecutionStore.result,
      loading: codeExecutionStore.isExecuting
    },
    
  },
  {
    value: 'chat',
    label: 'mentoria',
    component: markRaw(ChatTab),
    iconSrc: markRaw(MessageCircle),
    props: {
      result: codeExecutionStore.result,
      loading: codeExecutionStore.isExecuting
    },
    
  },
])

const defaultPanelTab = 'instructions'
const currentTab = ref(defaultPanelTab);

// Lifecycle hooks
onMounted(async () => {
  if (!isMounted.value || !authData.value?.id) return
  isLoading.value = true

  try {
    await fetchExerciseDetails()
  } catch (error) {
    console.error(`Error during component mount: ${error}`)
    await errorHandler.handleError(error)
  } finally {
    if (isMounted.value) {
      isLoading.value = false
    }
  }
})

onUnmounted(() => {
  isMounted.value = false
  codeExecutionStore.reset()
})

onBeforeRouteLeave(() => {
  codeExecutionStore.reset()
})

// Watchers
watch(initialCode, (newCode) => {
  if (isMounted.value) {
    editorCode.value = newCode
  }
})

watch(() => codeExecutionStore.activeTab, (newTab) => {
  if (newTab !== currentTab.value) {
    currentTab.value = newTab;
  }
});

const handleCodeExecution = async (code: string) => {
  if (!exercise.value) {
    await errorHandler.handleError(new AppError('No exercise loaded', {
      statusCode: 400,
      data: {
        action: 'execute_code',
        message: 'Attempted to execute code without an active exercise'
      }
    }))
    return
  }

  try {
    console.debug(`Executing code for exercise: ${exercise.value.id}`)
    currentTab.value = 'results'
    await executeCode({
      code,
      language: currentLanguage.value,
      exercise_id: exercise.value.id
    })
  } catch (error) {
    await errorHandler.handleError(error, {
      statusCode: 500,
      data: {
        action: 'execute_code',
        exerciseId: exercise.value.id,
        language: currentLanguage.value
      }
    })
  }
}
</script>

<template>
  <loading-overlay v-if="isLoading"/>
  <div v-else class="flex flex-col h-screen w-full bg-background">
    <ExerciseHeader 
      :lesson="currentLesson" 
      :exercise-name="currentExercise"
      :file-name="currentFileName"
      :on-back-click="handleBackClick" 
      :on-settings-click="handleSettingsClick"
    />

    <ResizablePanelGroup direction="horizontal" class="flex-grow">
      <ResizablePanel :default-size="70" :min-size="30">
        <div class="h-screen">
          <div
              class="inline-flex items-center px-2 sm:px-4 py-1 sm:py-1.5 rounded-br-2xl sm:rounded-br-3xl border-r-2 border-b-2 overflow-hidden bg-muted/50">
            <div
                class="font-medium text-xs sm:text-sm text-foreground truncate max-w-[120px] sm:max-w-none">
              {{ currentFileName }}
            </div>
          </div>
          <CodeEditor 
            v-if="initialCode" 
            v-model:code="editorCode" 
            :language="currentLanguage"
            :initial-code="initialCode" 
            :available-extensions="availableExtensions"
            :available-themes="availableThemes" 
            :active-extensions="activeExtensions"
            :active-theme="editorTheme" class="h-full overflow-hidden"
            @change="handleCodeChange"
            @execute="handleCodeExecution"
          />
        </div>
      </ResizablePanel>
      <ResizableHandle/>
      <ResizablePanel :default-size="30" :min-size="20">
        <div class="flex flex-col h-full">
          <ExercisePanel 
            v-model:active-tab="currentTab" 
            :tabs="panelTabs"
            :default-tab="defaultPanelTab"
            :is-open="isPanelOpen" class="flex-1 h-full"
            @update:is-open="isPanelOpen = $event"
          />
        </div>
      </ResizablePanel>
    </ResizablePanelGroup>
  </div>
</template>