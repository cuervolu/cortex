<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
import type {ViewUpdate} from '@codemirror/view'
import type {CodeMirrorRef, Statistics} from "#build/nuxt-codemirror";
import {javascript} from '@codemirror/lang-javascript'
import interact from '@replit/codemirror-interact';
import {loadLanguage} from '@uiw/codemirror-extensions-langs';
import {indentationMarkers} from '@replit/codemirror-indentation-markers';
import {zebraStripes} from '@uiw/codemirror-extensions-zebra-stripes';
import {lineNumbersRelative} from '@uiw/codemirror-extensions-line-numbers-relative'
import {okaidia} from '@uiw/codemirror-theme-okaidia';
import type {Extension} from "@codemirror/state";

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '// Type some code here'
  },
  language: {
    type: String,
    default: 'javascript'
  }
})

const emit = defineEmits(['update:modelValue', 'change', 'update'])

const code = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const codemirror = ref<CodeMirrorRef>()

const getLanguageExtension = (lang: string) => {
  switch (lang) {
    case 'javascript':
    case 'typescript':
      return javascript({jsx: true, typescript: true})
    case 'java':
      return loadLanguage('java')
    case 'rust':
      return loadLanguage('rust')
    case 'python':
      return loadLanguage('python')
    case 'csharp':
      return loadLanguage('csharp')
    case 'go':
      return loadLanguage("go")
    default:
      return javascript()
  }
}

const extensions = computed((): Extension[] => [
  lineNumbersRelative,
  getLanguageExtension(props.language) || javascript(),
  interact(),
  indentationMarkers(),
  zebraStripes()
]);

const handleChange = (value: string, viewUpdate: ViewUpdate) => {
  emit('change', value, viewUpdate)
}

const handleUpdate = (viewUpdate: ViewUpdate) => {
  emit('update', viewUpdate)
}

const handleStatistics = (stats: Statistics) => {
  console.log('Statistics:', stats)
}

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
      :extensions="extensions"
      :theme="okaidia"
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
}

.cm-scroller { overflow: auto; min-height: 350px; }

.cm-content, .cm-gutter { min-height: 150px; }
.cm-gutters { margin: 1px; }
</style>
