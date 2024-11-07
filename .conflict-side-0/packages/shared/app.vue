<script setup lang="ts">
import { ref, markRaw, computed } from 'vue'
import type { Component } from 'vue'
import { okaidia } from "@uiw/codemirror-theme-okaidia";
import { noctisLilac } from 'thememirror';
import Chat from "@cortex/desktop/src/components/ai/chat.vue";
import CodeEditor from "./components/CodeEditor.vue";
import {materialLight, materialDark} from "./themes";

interface Tab {
  value: string;
  label: string;
iconSrc?: string | Component;
  customIcon?: string;
  className?: string;
  labelClassName?: string;
  contentClassName?: string;
  component: Component;
  props?: Record<string, any>;
}

// Estado del ejercicio
const currentLesson = ref('Operators and Expressions')
const currentExercise = ref('Gigasecond')
const currentFileName = ref('exercise.rs')
const currentLanguage = ref('rust')
const initialCode = ref('// Initial code here')
const editorCode = ref(initialCode.value)

// Configuración del editor
const availableExtensions = ['lineNumbersRelative', 'indentationMarkers', 'interact']
const availableThemes = { okaidia, noctisLilac, materialLight, materialDark }
const activeExtensions = ref(['lineNumbersRelative', 'indentationMarkers', 'interact'])
const activeTheme = ref('materialLight')

// Estado del chat
const messages = ref<Array<{ sender: string; content: string }>>([])
const isStreaming = ref(false)
const currentStreamingMessage = ref('')

// Estado del panel
const isPanelOpen = ref(false)

// Handlers
const handleBackClick = () => {
  console.log('Back to exercise list')
}

const handleSettingsClick = () => {
  isPanelOpen.value = !isPanelOpen.value
}

const handleCodeChange = (newCode: string) => {
  editorCode.value = newCode
  // Aquí puedes enviar el código a tu API si es necesario
}

const handleSendMessage = (message: string) => {
  messages.value.push({ sender: 'user', content: message })
  // Aquí puedes enviar el mensaje a tu API de IA y manejar la respuesta
}

// Configuración del panel
const panelTabs = computed<Tab[]>(() => [
  {
    value: 'ia-help',
    label: 'IA Help',
    iconSrc: 'https://c.animaapp.com/opbLBgpn/img/icon-3.svg',
    className: 'bg-purple-200 rounded-full',
    labelClassName: 'font-medium text-purple-800',
    contentClassName: 'p-5',
    component: markRaw(Chat),
    props: {
      messages: messages.value,
      isStreaming: isStreaming.value,
      currentStreamingMessage: currentStreamingMessage.value,
      avatarSrc: 'path_to_avatar',
    }
  },
  // ... other tabs
])
const defaultPanelTab = 'ia-help'
</script>

<template>
  <div class="flex flex-col h-screen bg-[#f7f9fb]">
    <ExerciseHeader
      :lesson="currentLesson"
      :exercise-name="currentExercise"
      :file-name="currentFileName"
      :on-back-click="handleBackClick"
      :on-settings-click="handleSettingsClick"
    />
    <div class="flex flex-grow overflow-hidden">
      <div class="flex-grow p-5 bg-neutral-50">
        <div class="h-full rounded-md overflow-hidden">
          <CodeEditor
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
      </div>
      <ExercisePanel
        :tabs="panelTabs"
        :default-tab="defaultPanelTab"
        :is-open="isPanelOpen"
        class="w-96 flex-shrink-0 hidden sm:block"
        @send-message="handleSendMessage"
        @update:is-open="isPanelOpen = $event"
      />
    </div>
  </div>
</template>
