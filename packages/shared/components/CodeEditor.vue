<script setup lang="ts">
import { ref } from 'vue'
import type { ViewUpdate } from "@codemirror/view"
import type { Extension } from "@codemirror/state"
import type { CodeMirrorRef } from "#build/nuxt-codemirror"
import type { ThemeKey } from '../composables/editor'
import { useEditorCode, useEditorExtensions, useEditorTheme } from '../composables/editor'
import { useCodeExecutionStore } from "../stores/useCodeExecutionStore"
import ExecuteCodeButton from "./exercise/ExecuteCodeButton.vue"

const props = withDefaults(defineProps<{
  initialCode: string
  language: string
  placeholder?: string
  availableExtensions: string[]
  availableThemes: Record<ThemeKey, Extension>
  activeExtensions: string[]
  activeTheme: ThemeKey
  lineWrapping?: boolean
}>(), {
  placeholder: "// Type some code here",
  availableExtensions: () => ["lineNumbersRelative", "indentationMarkers", "interact"],
  activeExtensions: () => ["lineNumbersRelative", "indentationMarkers", "interact"],
  activeTheme: "materialDark",
  lineWrapping: true
})

const emit = defineEmits<{
  (e: 'update:code' | 'execute', value: string): void
  (e: 'change', value: string, viewUpdate: ViewUpdate): void
  (e: 'update', viewUpdate: ViewUpdate): void
}>()

const codeExecutionStore = useCodeExecutionStore()
const codemirror = ref<CodeMirrorRef>()

const { extensions } = useEditorExtensions({
  language: props.language,
  activeExtensions: props.activeExtensions,
  lineWrapping: props.lineWrapping
})
const { activeTheme } = useEditorTheme(props, codemirror)
const { code, handleChange, handleUpdate, handleStatistics } = useEditorCode(props, emit)

const handleExecute = () => {
  emit('execute', code.value)
}

onMounted(() => {
  if (codemirror.value) {
    console.log("Editor initialized:", codemirror.value.editor)
  }
})
</script>

<template>
  <div class="relative h-full">
    <ExecuteCodeButton
      :loading="codeExecutionStore.isExecuting"
      @execute="handleExecute"
    />
    <ClientOnly>
      <NuxtCodeMirror
        ref="codemirror"
        v-model="code"
        :extensions="extensions"
        :theme="activeTheme"
        :placeholder="placeholder"
        class="w-full h-full"
        :line-numbers="true"
        :auto-focus="true"
        :editable="true"
        :basic-setup="true"
        :indent-with-tab="true"
        @change="handleChange"
        @update="handleUpdate"
        @statistics="handleStatistics"
      />
    </ClientOnly>
  </div>
</template>

<style>
.cm-editor {
  height: 100%;
  border-radius: 10px;
  overflow: hidden;
}
.cm-scroller {
  min-height: 350px;
}
.cm-content,
.cm-gutter {
  min-height: 150px;
  font-family: "Source Code Pro", monospace;
  font-size: 1rem;
  font-weight: 600;
}
.cm-gutters {
  margin: 1px;
}
</style>
