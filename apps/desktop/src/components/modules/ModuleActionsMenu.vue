<script setup lang="ts">
import {
  MoreHorizontal,
  PlusCircle,
  Trash2,
  Edit,
  BookOpen,
  Layout,
  FileText,
  Globe,
  Lock
} from 'lucide-vue-next'

interface Props {
  moduleId: number
  isPublished: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'viewLessons', moduleId: number): void
  (e: 'createLesson', moduleId: number): void
  (e: 'togglePublish', moduleId: number): void
  (e: 'edit', moduleId: number): void
  (e: 'delete', moduleId: number): void
}>()
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger asChild>
      <Button variant="ghost" class="h-8 w-8 p-0">
        <span class="sr-only">Abrir menú</span>
        <MoreHorizontal class="h-4 w-4" />
      </Button>
    </DropdownMenuTrigger>

    <DropdownMenuContent align="end">
      <DropdownMenuLabel>Acciones</DropdownMenuLabel>

      <DropdownMenuGroup>
        <DropdownMenuItem @click="emit('viewLessons', moduleId)">
          <BookOpen class="mr-2 h-4 w-4" />
          <span>Ver lecciones</span>
        </DropdownMenuItem>
      </DropdownMenuGroup>

      <DropdownMenuSeparator />

      <DropdownMenuGroup>
        <DropdownMenuItem @click="emit('createLesson', moduleId)">
          <FileText class="mr-2 h-4 w-4" />
          <span>Crear lección</span>
        </DropdownMenuItem>
      </DropdownMenuGroup>

      <DropdownMenuSeparator />

      <DropdownMenuItem @click="emit('togglePublish', moduleId)">
        <component :is="isPublished ? Lock : Globe" class="mr-2 h-4 w-4" />
        <span>{{ isPublished ? 'Despublicar' : 'Publicar' }}</span>
      </DropdownMenuItem>

      <DropdownMenuItem @click="emit('edit', moduleId)">
        <Edit class="mr-2 h-4 w-4" />
        <span>Editar</span>
      </DropdownMenuItem>

      <DropdownMenuSeparator />

      <DropdownMenuItem
          @click="emit('delete', moduleId)"
          class="text-destructive focus:text-destructive"
      >
        <Trash2 class="mr-2 h-4 w-4" />
        <span>Eliminar</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>