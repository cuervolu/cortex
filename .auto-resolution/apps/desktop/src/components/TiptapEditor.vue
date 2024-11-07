<script setup lang="ts">
import {Image as TiptapImage} from '@tiptap/extension-image';
import {Underline as TiptapUnderline} from '@tiptap/extension-underline';
import {TextAlign as TiptapTextAlign} from '@tiptap/extension-text-align';
import {CharacterCount as TiptapCharacterCount} from '@tiptap/extension-character-count';
import {
  Bold,
  Italic,
  Strikethrough,
  Code,
  Pilcrow,
  List,
  ListOrdered,
  Quote,
  Terminal,
  MinusSquare,
  Eraser,
  Undo2,
  Redo2,
  Underline,
  AlignLeft,
  AlignCenter,
  AlignRight,
  AlignJustify,
  Image,
} from 'lucide-vue-next'
import TipTapButton from "~/components/TipTapButton.vue";
import {debug} from "@tauri-apps/plugin-log";

const lowlight = createLowlight(allLanguages);
const editor = useEditor(
    {
      editorProps: {
        attributes: {
          class: '"prose prose-sm sm:prose-base lg:prose-lg xl:prose-2xl prose-custom m-5 focus:outline-none',
        },
      },
      extensions: [
        TiptapStarterKit.configure({
          codeBlock: false,
        }),
        TiptapCodeBlockLowlight.configure({lowlight}),
        TiptapImage,
        TiptapUnderline,
        TiptapTextAlign,
        TiptapCharacterCount
      ],
    });


onBeforeUnmount(() => {
  unref(editor)?.destroy()
})


</script>

<template>
  <div class="border rounded-lg border-input bg-background p-2 space-y-4">
    <div v-if="editor" class="flex flex-wrap gap-2">
      <!-- Formato de texto básico -->
      <ToggleGroup type="multiple" class="flex flex-wrap gap-1">
        <TipTapButton tooltip="Negrita">
          <ToggleGroupItem
              value="bold"
              size="sm"
              :pressed="editor.isActive('bold')"
              :disabled="!editor.can().chain().focus().toggleBold().run()"
              @click="editor.chain().focus().toggleBold().run()"
          >
            <Bold class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
        <TipTapButton tooltip="Cursiva">
          <ToggleGroupItem
              value="italic"
              size="sm"
              :pressed="editor.isActive('italic')"
              :disabled="!editor.can().chain().focus().toggleItalic().run()"
              @click="editor.chain().focus().toggleItalic().run()"
          >

            <Italic class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <ToggleGroupItem
            value="strike"
            size="sm"
            :pressed="editor.isActive('strike')"
            :disabled="!editor.can().chain().focus().toggleStrike().run()"
            @click="editor.chain().focus().toggleStrike().run()"
        >
          <TipTapButton tooltip="Tachado">
            <Strikethrough class="h-4 w-4"/>
          </TipTapButton>
        </ToggleGroupItem>

        <ToggleGroupItem
            value="code"
            size="sm"
            :pressed="editor.isActive('code')"
            :disabled="!editor.can().chain().focus().toggleCode().run()"
            @click="editor.chain().focus().toggleCode().run()"
        >
          <TipTapButton tooltip="Código">
            <Code class="h-4 w-4"/>
          </TipTapButton>
        </ToggleGroupItem>
      </ToggleGroup>

      <Separator orientation="vertical" class="h-8"/>

      <!-- Encabezados -->
      <ToggleGroup type="single" class="flex flex-wrap gap-1">
        <TipTapButton tooltip="Párrafo">
          <ToggleGroupItem
              value="p"
              size="sm"
              :pressed="editor.isActive('paragraph')"
              @click="editor.chain().focus().setParagraph().run()"
          >
            <Pilcrow class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <ToggleGroupItem
            v-for="level in [1, 2, 3]"
            :key="level"
            :value="'h'+level"
            size="sm"
            :pressed="editor.isActive('heading', { level })"
            @click="editor.chain().focus().toggleHeading({ level: level as 1 | 2 | 3 }).run()"
        >
          <TipTapButton :tooltip="'Encabezado ' + level">
            H{{ level }}
          </TipTapButton>
        </ToggleGroupItem>
      </ToggleGroup>

      <Separator orientation="vertical" class="h-8"/>

      <!-- Listas -->
      <ToggleGroup type="multiple" class="flex flex-wrap gap-1">
        <TipTapButton tooltip="Lista con viñetas">
          <ToggleGroupItem
              value="bullet"
              size="sm"
              :pressed="editor.isActive('bulletList')"
              @click="editor.chain().focus().toggleBulletList().run()"
          >
            <List class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <TipTapButton tooltip="Lista numerada">
          <ToggleGroupItem
              value="ordered"
              size="sm"
              :pressed="editor.isActive('orderedList')"
              @click="editor.chain().focus().toggleOrderedList().run()"
          >
            <ListOrdered class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
      </ToggleGroup>

      <Separator orientation="vertical" class="h-8"/>

      <!-- Elementos de bloque -->
      <ToggleGroup type="multiple" class="flex flex-wrap gap-1">
        <TipTapButton tooltip="Cita">
          <ToggleGroupItem
              value="blockquote"
              size="sm"
              :pressed="editor.isActive('blockquote')"
              @click="editor.chain().focus().toggleBlockquote().run()"
          >
            <Quote class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <TipTapButton tooltip="Bloque de código">
          <ToggleGroupItem
              value="code-block"
              size="sm"
              :pressed="editor.isActive('codeBlock')"
              @click="editor.chain().focus().toggleCodeBlock().run()"
          >
            <Terminal class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
      </ToggleGroup>

      <Separator orientation="vertical" class="h-8"/>
      <!-- Alineación de texto -->
      <ToggleGroup type="single" class="flex flex-wrap gap-1">
        <TipTapButton tooltip="Alinear a la izquierda">
          <ToggleGroupItem
              value="left"
              size="sm"
              :pressed="editor.isActive({ textAlign: 'left' })"
              @click="editor.chain().focus().setTextAlign('left').run()"
          >
            <AlignLeft class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <TipTapButton tooltip="Centrar">
          <ToggleGroupItem
              value="center"
              size="sm"
              :pressed="editor.isActive({ textAlign: 'center' })"
              @click="editor.chain().focus().setTextAlign('center').run()"
          >
            <AlignCenter class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <TipTapButton tooltip="Alinear a la derecha">
          <ToggleGroupItem
              value="right"
              size="sm"
              :pressed="editor.isActive({ textAlign: 'right' })"
              @click="editor.chain().focus().setTextAlign('right').run()"
          >
            <AlignRight class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <TipTapButton tooltip="Justificar">
          <ToggleGroupItem
              value="justify"
              size="sm"
              :pressed="editor.isActive({ textAlign: 'justify' })"
              @click="editor.chain().focus().setTextAlign('justify').run()"
          >
            <AlignJustify class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>
      </ToggleGroup>

      <Separator orientation="vertical" class="h-8"/>


      <!-- Utilidades -->
      <div class="flex gap-1">
        <TipTapButton tooltip="Línea horizontal">
          <Button
              variant="outline"
              size="sm"
              @click="editor.chain().focus().setHorizontalRule().run()"
          >
            <MinusSquare class="h-4 w-4"/>
          </Button>
        </TipTapButton>

        <TipTapButton tooltip="Limpiar formato">
          <Button
              variant="outline"
              size="sm"
              @click="editor.chain().focus().unsetAllMarks().run()"
          >
            <Eraser class="h-4 w-4"/>
          </Button>
        </TipTapButton>
      </div>

      <Separator orientation="vertical" class="h-8"/>

      <!-- Formato adicional -->
      <ToggleGroup type="multiple" class="flex flex-wrap gap-1">
        <TipTapButton tooltip="Subrayar">
          <ToggleGroupItem
              value="underline"
              size="sm"
              :pressed="editor.isActive('underline')"
              @click="editor.chain().focus().toggleUnderline().run()"
          >
            <Underline class="h-4 w-4"/>
          </ToggleGroupItem>
        </TipTapButton>

        <TipTapButton tooltip="Insertar imagen">
          <Button
              variant="outline"
              size="sm"
              @click="() => {
        debug('Insertar imagen')
      }"
          >
            <Image class="h-4 w-4"/>
          </Button>
        </TipTapButton>
      </ToggleGroup>
      <Separator orientation="vertical" class="h-8"/>
      <!-- Deshacer/Rehacer -->
      <div class="flex gap-1">
        <Button
            variant="outline"
            size="sm"
            :disabled="!editor.can().chain().focus().undo().run()"
            @click="editor.chain().focus().undo().run()"
        >
          <Undo2 class="h-4 w-4"/>
        </Button>

        <Button
            variant="outline"
            size="sm"
            :disabled="!editor.can().chain().focus().redo().run()"
            @click="editor.chain().focus().redo().run()"
        >
          <Redo2 class="h-4 w-4"/>
        </Button>
      </div>
    </div>


    <!-- Editor Content -->
    <div class="space-y-2">
      <TiptapEditorContent
          :editor="editor"
          class="max-w-none min-h-[400px] max-h-[600px]
    overflow-y-auto p-6 border rounded-md"
      />
      <div class="text-sm text-muted-foreground text-right">
        {{ editor?.storage.characterCount.characters() ?? 0 }} caracteres
      </div>
    </div>
  </div>
</template>

<style scoped>
.prose-custom {
  & :where(h1, h2, h3, h4, h5, h6, p, blockquote, strong, em, code, pre) {
    color: hsl(var(--foreground));
  }
}

</style>