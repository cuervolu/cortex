<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import type { ViewUpdate } from '@codemirror/view'
import type { CodeMirrorRef, Statistics } from "#build/nuxt-codemirror";
import { javascript } from '@codemirror/lang-javascript'
import interact from '@replit/codemirror-interact';
import { loadLanguage } from '@uiw/codemirror-extensions-langs';
import { indentationMarkers } from '@replit/codemirror-indentation-markers';
import { lineNumbersRelative } from '@uiw/codemirror-extensions-line-numbers-relative'
import { okaidia } from '@uiw/codemirror-theme-okaidia';
import type { Extension as CodeMirrorExtension } from "@codemirror/state";
import type {LanguageSupport} from "@codemirror/language";
import {noctisLilac} from 'thememirror';

interface Props {
  initialCode: string;
  language: string;
  placeholder?: string;
  availableExtensions: string[];
  availableThemes: Record<string, CodeMirrorExtension>;
  activeExtensions: string[];
  activeTheme: string;
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '// Type some code here',
  availableExtensions: () => ['lineNumbersRelative', 'indentationMarkers', 'interact'],
  availableThemes: () => ({ noctisLilac, okaidia }),
  activeExtensions: () => ['lineNumbersRelative', 'indentationMarkers', 'interact'],
  activeTheme: 'noctisLilac'
})

const emit = defineEmits(['update:code', 'change', 'update'])

const code = ref(props.initialCode)
const codemirror = ref<CodeMirrorRef>()

const getLanguageExtension = (lang: string): CodeMirrorExtension => {
  let extension: LanguageSupport | CodeMirrorExtension;
  switch (lang) {
    case 'javascript':
    case 'typescript':
      extension = javascript({ jsx: true, typescript: true });
      break;
    case 'java':
      extension = loadLanguage('java') || javascript();
      break;
    case 'rust':
      extension = loadLanguage('rust') || javascript();
      break;
    case 'python':
      extension = loadLanguage('python') || javascript();
      break;
    case 'csharp':
      extension = loadLanguage('csharp') || javascript();
      break;
    case 'go':
      extension = loadLanguage("go") || javascript();
      break;
    default:
      extension = javascript();
  }
  return extension;
}

const activeExtensions = computed((): CodeMirrorExtension[] => {
  const extensions: CodeMirrorExtension[] = [getLanguageExtension(props.language)]
  if (props.activeExtensions.includes('lineNumbersRelative')) extensions.push(lineNumbersRelative)
  if (props.activeExtensions.includes('indentationMarkers')) extensions.push(indentationMarkers())
  if (props.activeExtensions.includes('interact')) extensions.push(interact())
  return extensions
})


const activeTheme = computed(() => props.availableThemes[props.activeTheme] || okaidia)

const handleChange = (value: string, viewUpdate: ViewUpdate) => {
  emit('update:code', value)
  emit('change', value, viewUpdate)
}

const handleUpdate = (viewUpdate: ViewUpdate) => {
  emit('update', viewUpdate)
}

const handleStatistics = (stats: Statistics) => {
  console.log('Statistics:', stats)
}

watch(() => code.value, (newCode) => {
  emit('update:code', newCode)
})

onMounted(() => {
  if (codemirror.value) {
    console.log('Editor initialized:', codemirror.value.editor)
  }
})
</script>


<template>
  <ClientOnly>
    <NuxtCodeMirror
      ref="codemirror"
      v-model="code"
      :extensions="activeExtensions"
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
</template>



<style>
.cm-editor {
  height: 100%;
  border-radius: 10px;
  overflow: hidden;
}
.cm-scroller {
  overflow: hidden;
  min-height: 350px;
}
.cm-content, .cm-gutter {
  min-height: 150px;
  font-family: "Source Code Pro", monospace;
  font-size: 1.2rem;
  font-weight: 600;
}
.cm-gutters {
  margin: 1px;
}
</style>
