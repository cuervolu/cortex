<script setup lang="ts">
import type {Editor} from '@tiptap/core'
import {useTipTap} from '~/composables'
import EditorToolbar from './EditorToolbar.vue'
import EditorBubbleMenu from './EditorBubbleMenu.vue'

interface Props {
  initialContent?: string
  placeholder?: string
  minHeight?: number
  maxHeight?: number
  readonly?: boolean
  className?: string
}

interface Emits {
  (e: 'update', html: string): void

  (e: 'ready', editor: Editor): void
}

const props = withDefaults(defineProps<Props>(), {
  initialContent: '',
  placeholder: 'Escribe aquí el contenido...',
  minHeight: 400,
  maxHeight: 600,
  readonly: false,
  className: ''
})

const emit = defineEmits<Emits>()

const {editor, handleImageInsert, getCharacterCount} = useTipTap({
  initialContent: props.initialContent,
  placeholder: props.placeholder,
  readonly: props.readonly,
  className: props.className,
  onUpdate: (html) => emit('update', html),
  onReady: (editor) => emit('ready', editor)
})

defineExpose({
  editor,
  focus: () => editor.value?.chain().focus().run(),
  clear: () => editor.value?.chain().clearContent().focus().run(),
  setContent: (content: string) => editor.value?.chain().setContent(content).focus().run(),
  getHTML: () => editor.value?.getHTML(),
  getJSON: () => editor.value?.getJSON(),
})
</script>

<template>
  <div class="border rounded-lg border-input bg-background p-2 space-y-4">
    <template v-if="editor">
      <!-- Toolbar -->
      <EditorToolbar
          v-if="!readonly"
          :editor="editor"
          :on-image-insert="handleImageInsert"
      />

      <!-- Bubble Menu -->
      <EditorBubbleMenu :editor="editor"/>

      <!-- Editor Content -->
      <div class="space-y-2">
        <TiptapEditorContent
            :editor="editor"
            :class="[
            'overflow-y-auto p-6 border rounded-md',
            { 'cursor-not-allowed opacity-60': readonly }
          ]"
            :style="{
            minHeight: `${minHeight}px`,
            maxHeight: `${maxHeight}px`
          }"
        />
        <div class="text-sm text-muted-foreground text-right">
          {{ getCharacterCount() }} caracteres
        </div>
      </div>
    </template>
  </div>
</template>

<style>
.prose-custom {
  & :where(h1, h2, h3, h4, h5, h6, p, blockquote, strong, em, code, pre) {
    color: hsl(var(--foreground));
  }
}

.tiptap p.is-editor-empty:first-child::before {
  color: hsl(var(--muted-foreground));
  content: attr(data-placeholder);
  float: left;
  height: 0;
  pointer-events: none;
}

.readonly {
  & .tiptap {
    @apply bg-muted;
  }

  & .tippy-content {
    display: none;
  }
}
</style>