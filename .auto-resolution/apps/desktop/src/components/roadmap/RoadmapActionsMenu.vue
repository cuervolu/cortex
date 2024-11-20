<script setup lang="ts">
import {
  MoreVerticalIcon,
  EyeIcon,
  EyeOffIcon,
  TrashIcon,
  BookOpenIcon,
  FolderIcon,
  EditIcon,
  ScrollTextIcon,
  Pointer
} from 'lucide-vue-next'

const showDeleteDialog = ref(false)
const props = defineProps<{
  roadmapId: number
  isPublished: boolean
}>()

const emit = defineEmits<{
  (e: 'createCourse', id: number): void
  (e: 'createModule', id: number): void
  (e: 'createLesson', id: number): void
  (e: 'togglePublish', id: number): void
  (e: 'assign-courses', id: number): void
  (e: 'edit', id: number): void
  (e: 'delete', id: number): void
}>()

const handleDeleteClick = () => {
  showDeleteDialog.value = true
}

const confirmDelete = () => {
  emit('delete', props.roadmapId)
  showDeleteDialog.value = false
}
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger asChild>
      <Button variant="ghost" class="h-8 w-8 p-0">
        <span class="sr-only">Abrir menú</span>
        <MoreVerticalIcon class="h-4 w-4"/>
      </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent align="end">
      <DropdownMenuLabel>Acciones</DropdownMenuLabel>

      <!-- Creación -->
      <DropdownMenuItem @click="emit('createCourse', roadmapId)">
        <BookOpenIcon class="mr-2 h-4 w-4"/>
        <span>Crear Curso</span>
      </DropdownMenuItem>
      <DropdownMenuItem @click="emit('createModule', roadmapId)">
        <FolderIcon class="mr-2 h-4 w-4"/>
        <span>Crear Módulo</span>
      </DropdownMenuItem>
      <DropdownMenuItem @click="emit('createLesson', roadmapId)">
        <ScrollTextIcon class="mr-2 h-4 w-4"/>
        <span>Crear Lección</span>
      </DropdownMenuItem>

      <DropdownMenuItem @click="emit('assign-courses', roadmapId)">
        <Pointer class="mr-2 h-4 w-4"/>
        <span>Asignar Cursos</span>
      </DropdownMenuItem>

      <DropdownMenuSeparator/>

      <!-- Acciones -->
      <DropdownMenuItem @click="emit('togglePublish', roadmapId)">
        <component
            :is="isPublished ? EyeOffIcon : EyeIcon"
            class="mr-2 h-4 w-4"
        />
        <span>{{ isPublished ? 'Despublicar' : 'Publicar' }}</span>
      </DropdownMenuItem>

      <DropdownMenuItem @click="emit('edit', roadmapId)">
        <EditIcon class="mr-2 h-4 w-4"/>
        <span>Editar</span>
      </DropdownMenuItem>

      <DropdownMenuItem
          class="text-destructive focus:text-destructive"
           @click="handleDeleteClick"
      >
        <TrashIcon class="mr-2 h-4 w-4"/>
        <span>Eliminar</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>

  <AlertDialog :open="showDeleteDialog" @update:open="showDeleteDialog = $event">
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>¿Eliminar roadmap?</AlertDialogTitle>
        <AlertDialogDescription>
          Esta acción no se puede deshacer. Se eliminará permanentemente el roadmap y todos sus datos asociados.
        </AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel @click="showDeleteDialog = false">
          Cancelar
        </AlertDialogCancel>
        <AlertDialogAction @click="confirmDelete">
          Eliminar
        </AlertDialogAction>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>


</template>