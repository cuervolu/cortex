<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { javascript } from '@codemirror/lang-javascript'
import { dracula } from '@uiw/codemirror-theme-dracula'
import { lineNumbersRelative } from '@uiw/codemirror-extensions-line-numbers-relative'
import type { ViewUpdate } from '@codemirror/view'
import type {CodeMirrorRef, Statistics} from "#build/nuxt-codemirror";

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
      return javascript({ jsx: true, typescript: true })
    // Aquí puedes agregar más casos para otros lenguajes
    default:
      return javascript()
  }
}

const extensions = computed(() => [
  lineNumbersRelative,
  getLanguageExtension(props.language)
])

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
      :theme="dracula"
      :placeholder="placeholder"
      class="w-full h-full"
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
