<script setup lang="ts">
import type {Editor} from '@tiptap/core'
import {
  Bold, Italic, Strikethrough, Code, Pilcrow,
  List, ListOrdered, Quote, Terminal, MinusSquare,
  Eraser, Undo2, Redo2, Underline, AlignLeft,
  AlignCenter, AlignRight, AlignJustify, Image, type LucideIcon
} from 'lucide-vue-next'


interface BaseToolbarItem {
  action: () => boolean
  isActive: () => boolean
  tooltip: string
  isEnabled?: () => boolean
}

interface IconToolbarItem extends BaseToolbarItem {
  icon: LucideIcon
  label?: never
}

interface LabelToolbarItem extends BaseToolbarItem {
  label: string
  icon?: never
}

type ToolbarItem = IconToolbarItem | LabelToolbarItem

interface Props {
  editor: Editor
  onImageInsert?: () => Promise<void>
}

const props = defineProps<Props>()

const textFormatGroup = [
  {
    icon: Bold,
    action: () => props.editor.chain().focus().toggleBold().run(),
    isActive: () => props.editor.isActive('bold'),
    tooltip: 'Negrita',
    isEnabled: () => props.editor.can().chain().focus().toggleBold().run()
  },
  {
    icon: Italic,
    action: () => props.editor.chain().focus().toggleItalic().run(),
    isActive: () => props.editor.isActive('italic'),
    tooltip: 'Cursiva',
    isEnabled: () => props.editor.can().chain().focus().toggleItalic().run()
  },
  {
    icon: Underline,
    action: () => props.editor.chain().focus().toggleUnderline().run(),
    isActive: () => props.editor.isActive('underline'),
    tooltip: 'Subrayado'
  },
  {
    icon: Strikethrough,
    action: () => props.editor.chain().focus().toggleStrike().run(),
    isActive: () => props.editor.isActive('strike'),
    tooltip: 'Tachado'
  },
  {
    icon: Code,
    action: () => props.editor.chain().focus().toggleCode().run(),
    isActive: () => props.editor.isActive('code'),
    tooltip: 'Código'
  }
]

const headingGroup: ToolbarItem[] = [
  {
    icon: Pilcrow,
    action: () => props.editor.chain().focus().setParagraph().run(),
    isActive: () => props.editor.isActive('paragraph'),
    tooltip: 'Párrafo'
  },
  ...Array.from({length: 3}, (_, i) => ({
    label: `H${i + 1}`,
    action: () => props.editor.chain().focus().toggleHeading({level: i + 1 as 1 | 2 | 3}).run(),
    isActive: () => props.editor.isActive('heading', {level: i + 1}),
    tooltip: `Encabezado ${i + 1}`
  }))
]

const listGroup = [
  {
    icon: List,
    action: () => props.editor.chain().focus().toggleBulletList().run(),
    isActive: () => props.editor.isActive('bulletList'),
    tooltip: 'Lista con viñetas'
  },
  {
    icon: ListOrdered,
    action: () => props.editor.chain().focus().toggleOrderedList().run(),
    isActive: () => props.editor.isActive('orderedList'),
    tooltip: 'Lista numerada'
  }
]

const blockGroup = [
  {
    icon: Quote,
    action: () => props.editor.chain().focus().toggleBlockquote().run(),
    isActive: () => props.editor.isActive('blockquote'),
    tooltip: 'Cita'
  },
  {
    icon: Terminal,
    action: () => props.editor.chain().focus().toggleCodeBlock().run(),
    isActive: () => props.editor.isActive('codeBlock'),
    tooltip: 'Bloque de código'
  }
]

const alignmentGroup = [
  {
    icon: AlignLeft,
    action: () => props.editor.chain().focus().setTextAlign('left').run(),
    isActive: () => props.editor.isActive({textAlign: 'left'}),
    tooltip: 'Alinear a la izquierda'
  },
  {
    icon: AlignCenter,
    action: () => props.editor.chain().focus().setTextAlign('center').run(),
    isActive: () => props.editor.isActive({textAlign: 'center'}),
    tooltip: 'Centrar'
  },
  {
    icon: AlignRight,
    action: () => props.editor.chain().focus().setTextAlign('right').run(),
    isActive: () => props.editor.isActive({textAlign: 'right'}),
    tooltip: 'Alinear a la derecha'
  },
  {
    icon: AlignJustify,
    action: () => props.editor.chain().focus().setTextAlign('justify').run(),
    isActive: () => props.editor.isActive({textAlign: 'justify'}),
    tooltip: 'Justificar'
  }
]

const utilityGroup = [
  {
    icon: MinusSquare,
    action: () => props.editor.chain().focus().setHorizontalRule().run(),
    tooltip: 'Línea horizontal'
  },
  {
    icon: Eraser,
    action: () => props.editor.chain().focus().unsetAllMarks().run(),
    tooltip: 'Limpiar formato'
  },
  {
    icon: Image,
    action: () => props.onImageInsert?.(),
    tooltip: 'Insertar imagen'
  }
]

const historyGroup = [
  {
    icon: Undo2,
    action: () => props.editor.chain().focus().undo().run(),
    isEnabled: () => props.editor.can().chain().focus().undo().run(),
    tooltip: 'Deshacer'
  },
  {
    icon: Redo2,
    action: () => props.editor.chain().focus().redo().run(),
    isEnabled: () => props.editor.can().chain().focus().redo().run(),
    tooltip: 'Rehacer'
  }
]
</script>

<template>
  <div class="flex flex-wrap gap-2">
    <!-- Formato de texto básico -->
    <ToggleGroup type="multiple" class="flex flex-wrap gap-1">
      <template v-for="item in textFormatGroup" :key="item.tooltip">
        <TipTapButton :tooltip="item.tooltip">
          <ToggleGroupItem
              value="format"
              size="sm"
              :pressed="item.isActive()"
              :disabled="item.isEnabled?.() === false"
              @click="item.action"
          >
            <component :is="item.icon" class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
      </template>
    </ToggleGroup>

    <Separator orientation="vertical" class="h-8"/>

    <!-- Encabezados -->
    <ToggleGroup type="single" class="flex flex-wrap gap-1">
      <template v-for="item in headingGroup" :key="item.tooltip">
        <TipTapButton :tooltip="item.tooltip">
          <ToggleGroupItem
              :value="item.label ? `h${item.label.slice(1)}` : 'p'"
              size="sm"
              :pressed="item.isActive()"
              @click="item.action"
          >
            <component
                :is="item.icon"
                v-if="item.icon"
                class="h-4 w-4"
            />
            <template v-else>{{ item.label }}</template>
          </ToggleGroupItem>
        </TipTapButton>
      </template>
    </ToggleGroup>

    <Separator orientation="vertical" class="h-8"/>

    <!-- Listas -->
    <ToggleGroup type="multiple" class="flex flex-wrap gap-1">
      <template v-for="item in listGroup" :key="item.tooltip">
        <TipTapButton :tooltip="item.tooltip">
          <ToggleGroupItem
              :value="item.tooltip"
              size="sm"
              :pressed="item.isActive()"
              @click="item.action"
          >
            <component :is="item.icon" class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
      </template>
    </ToggleGroup>

    <Separator orientation="vertical" class="h-8"/>

    <!-- Elementos de bloque -->
    <ToggleGroup type="multiple" class="flex flex-wrap gap-1">
      <template v-for="item in blockGroup" :key="item.tooltip">
        <TipTapButton :tooltip="item.tooltip">
          <ToggleGroupItem
              :value="item.tooltip"
              size="sm"
              :pressed="item.isActive()"
              @click="item.action"
          >
            <component :is="item.icon" class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
      </template>
    </ToggleGroup>

    <Separator orientation="vertical" class="h-8"/>

    <!-- Alineación -->
    <ToggleGroup type="single" class="flex flex-wrap gap-1">
      <template v-for="item in alignmentGroup" :key="item.tooltip">
        <TipTapButton :tooltip="item.tooltip">
          <ToggleGroupItem
              :value="item.tooltip"
              size="sm"
              :pressed="item.isActive()"
              @click="item.action"
          >
            <component :is="item.icon" class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
      </template>
    </ToggleGroup>

    <Separator orientation="vertical" class="h-8"/>

    <!-- Utilidades -->
    <div class="flex gap-1">
      <template v-for="item in utilityGroup" :key="item.tooltip">
        <TipTapButton :tooltip="item.tooltip">
          <Button
              variant="outline"
              size="sm"
              @click="item.action"
          >
            <component :is="item.icon" class="h-4 w-4"/>
          </Button>
        </TipTapButton>
      </template>
    </div>

    <Separator orientation="vertical" class="h-8"/>

    <!-- Historial -->
    <div class="flex gap-1">
      <template v-for="item in historyGroup" :key="item.tooltip">
        <TipTapButton :tooltip="item.tooltip">
          <Button
              variant="outline"
              size="sm"
              :disabled="item.isEnabled?.() === false"
              @click="item.action"
          >
            <component :is="item.icon" class="h-4 w-4"/>
          </Button>
        </TipTapButton>
      </template>
    </div>
  </div>
</template>