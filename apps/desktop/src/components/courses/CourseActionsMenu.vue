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
  courseId: number
  isPublished: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'viewLessons', courseId: number): void
  (e: 'viewModules', courseId: number): void
  (e: 'createLesson', courseId: number): void
  (e: 'createModule', courseId: number): void
  (e: 'togglePublish', courseId: number): void
  (e: 'edit', courseId: number): void
  (e: 'delete', courseId: number): void
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
        <DropdownMenuItem @click="emit('viewModules', courseId)">
          <Layout class="mr-2 h-4 w-4" />
          <span>Ver módulos</span>
        </DropdownMenuItem>
        <DropdownMenuItem @click="emit('viewLessons', courseId)">
          <BookOpen class="mr-2 h-4 w-4" />
          <span>Ver lecciones</span>
        </DropdownMenuItem>
      </DropdownMenuGroup>

      <DropdownMenuSeparator />

      <DropdownMenuGroup>
        <DropdownMenuItem @click="emit('createModule', courseId)">
          <PlusCircle class="mr-2 h-4 w-4" />
          <span>Crear módulo</span>
        </DropdownMenuItem>
        <DropdownMenuItem @click="emit('createLesson', courseId)">
          <FileText class="mr-2 h-4 w-4" />
          <span>Crear lección</span>
        </DropdownMenuItem>
      </DropdownMenuGroup>

      <DropdownMenuSeparator />

      <DropdownMenuItem @click="emit('togglePublish', courseId)">
        <component :is="isPublished ? Lock : Globe" class="mr-2 h-4 w-4" />
        <span>{{ isPublished ? 'Despublicar' : 'Publicar' }}</span>
      </DropdownMenuItem>

      <DropdownMenuItem @click="emit('edit', courseId)">
        <Edit class="mr-2 h-4 w-4" />
        <span>Editar</span>
      </DropdownMenuItem>

      <DropdownMenuSeparator />

      <DropdownMenuItem
          @click="emit('delete', courseId)"
          class="text-destructive focus:text-destructive"
      >
        <Trash2 class="mr-2 h-4 w-4" />
        <span>Eliminar</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>