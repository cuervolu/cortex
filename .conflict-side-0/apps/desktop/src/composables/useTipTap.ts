import { debug } from "@tauri-apps/plugin-log"

import type { Editor } from '@tiptap/core'
import StarterKit from '@tiptap/starter-kit'
import { Image as TiptapImage } from '@tiptap/extension-image'
import { Underline as TiptapUnderline } from '@tiptap/extension-underline'
import { TextAlign as TiptapTextAlign } from '@tiptap/extension-text-align'
import { CharacterCount as TiptapCharacterCount } from '@tiptap/extension-character-count'
import { Placeholder as TiptapPlaceholder } from '@tiptap/extension-placeholder'
import { Typography as TiptapTypography } from "@tiptap/extension-typography"
import { BubbleMenu as TiptapBubbleMenu } from '@tiptap/extension-bubble-menu'
import { CodeBlockLowlight as TiptapCodeBlockLowlight } from '@tiptap/extension-code-block-lowlight'
import { createLowlight } from 'lowlight'

export interface EditorOptions {
  initialContent?: string
  placeholder?: string
  readonly?: boolean
  className?: string
  onUpdate?: (html: string) => void
  onReady?: (editor: Editor) => void
}

export function useTipTap(options: EditorOptions = {}) {
  const {
    initialContent = '',
    placeholder = 'Escribe aquí el contenido...',
    readonly = false,
    className = '',
    onUpdate,
    onReady
  } = options

  const lowlight = createLowlight(allLanguages)

  const editor = useEditor({
    content: initialContent,
    editable: !readonly,
    editorProps: {
      attributes: {
        class: `prose prose-sm sm:prose-base lg:prose-lg xl:prose-2xl prose-custom m-5 focus:outline-none ${className}`,
      },
    },
    extensions: [
      StarterKit.configure({
        codeBlock: false,
      }),
      TiptapPlaceholder.configure({
        placeholder,
      }),
      TiptapImage,
      TiptapCodeBlockLowlight.configure({ lowlight }),
      TiptapUnderline,
      TiptapTextAlign.configure({
        types: ['heading', 'paragraph'],
      }),
      TiptapCharacterCount,
      TiptapTypography,
      TiptapBubbleMenu
    ],
    onUpdate: ({ editor }) => {
      const html = editor.getHTML()
      onUpdate?.(html)
    },
    onCreate: ({ editor }) => {
      onReady?.(editor)
    }
  })

  // Utilidades del editor
  const utils = {
    focus: () => editor.value?.chain().focus().run(),
    clear: () => editor.value?.chain().clearContent().focus().run(),
    setContent: (content: string) => editor.value?.chain().setContent(content).focus().run(),
    getHTML: () => editor.value?.getHTML(),
    getJSON: () => editor.value?.getJSON(),
    getCharacterCount: () => editor.value?.storage.characterCount.characters() ?? 0,
    handleImageInsert: async () => {
      await debug('Inserting image...')
    }
  }

  // Watches
  watch(() => readonly, (newValue) => {
    if (editor.value) {
      editor.value.setEditable(!newValue)
    }
  })

  // Cleanup
  onBeforeUnmount(async () => {
    await debug('Destroying editor')
    unref(editor)?.destroy()
  })

  return {
    editor,
    ...utils
  }
}