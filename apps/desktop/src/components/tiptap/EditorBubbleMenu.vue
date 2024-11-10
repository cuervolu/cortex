<script setup lang="ts">
import type { Editor } from '@tiptap/core'
import {
  Bold,
  Italic,
  Strikethrough,
  Code,
  Underline,
} from 'lucide-vue-next'

defineProps<{
  editor: Editor | null
}>()

const bubbleMenuConfig = {
  tippyOptions: {
    duration: 100,
    theme: 'dark',
    maxWidth: 'none',
  }
}
</script>

<template>
  <TiptapBubbleMenu
      v-if="editor"
      :editor="editor"
      :tippy-options="bubbleMenuConfig.tippyOptions"
      class="bubble-menu-container"
  >
    <div class="flex gap-1 p-1 rounded-lg border shadow-lg bg-popover">
      <TooltipProvider>
        <!-- Bold -->
        <Tooltip>
          <TooltipTrigger as-child>
            <Button
                variant="ghost"
                size="icon"
                class="h-8 w-8"
                :class="{ 'bg-accent': editor.isActive('bold') }"
                @click="editor.chain().focus().toggleBold().run()"
            >
              <Bold class="h-4 w-4" />
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <p>Negrita</p>
          </TooltipContent>
        </Tooltip>

        <!-- Italic -->
        <Tooltip>
          <TooltipTrigger as-child>
            <Button
                variant="ghost"
                size="icon"
                class="h-8 w-8"
                :class="{ 'bg-accent': editor.isActive('italic') }"
                @click="editor.chain().focus().toggleItalic().run()"
            >
              <Italic class="h-4 w-4" />
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <p>Cursiva</p>
          </TooltipContent>
        </Tooltip>

        <!-- Underline -->
        <Tooltip>
          <TooltipTrigger as-child>
            <Button
                variant="ghost"
                size="icon"
                class="h-8 w-8"
                :class="{ 'bg-accent': editor.isActive('underline') }"
                @click="editor.chain().focus().toggleUnderline().run()"
            >
              <Underline class="h-4 w-4" />
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <p>Subrayado</p>
          </TooltipContent>
        </Tooltip>

        <!-- Strikethrough -->
        <Tooltip>
          <TooltipTrigger as-child>
            <Button
                variant="ghost"
                size="icon"
                class="h-8 w-8"
                :class="{ 'bg-accent': editor.isActive('strike') }"
                @click="editor.chain().focus().toggleStrike().run()"
            >
              <Strikethrough class="h-4 w-4" />
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <p>Tachado</p>
          </TooltipContent>
        </Tooltip>

        <!-- Code -->
        <Tooltip>
          <TooltipTrigger as-child>
            <Button
                variant="ghost"
                size="icon"
                class="h-8 w-8"
                :class="{ 'bg-accent': editor.isActive('code') }"
                @click="editor.chain().focus().toggleCode().run()"
            >
              <Code class="h-4 w-4" />
            </Button>
          </TooltipTrigger>
          <TooltipContent>
            <p>Código</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
    </div>
  </TiptapBubbleMenu>
</template>

<style>
.bubble-menu-container {
  display: flex;
  z-index: 50;
}
</style>