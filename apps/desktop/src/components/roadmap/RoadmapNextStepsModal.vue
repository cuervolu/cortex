<script setup lang="ts">
defineProps<{
  isOpen: boolean
}>();

defineEmits<{
  (e: 'close'): void
  (e: 'select', action: 'assign' | 'create'): void
}>();
</script>

<template>
  <Dialog :open="isOpen" @update:open="$emit('close')">
    <DialogContent class="sm:max-w-md">
      <DialogHeader>
        <DialogTitle>¿Qué deseas hacer ahora?</DialogTitle>
        <DialogDescription>
          Puedes asignar cursos existentes a tu roadmap o crear uno nuevo.
        </DialogDescription>
      </DialogHeader>
      <div class="flex flex-col gap-4 py-4">
        <Button class="w-full" @click="$emit('select', 'assign')">
          Asignar cursos existentes
        </Button>
        <Button class="w-full" variant="outline" @click="$emit('select', 'create')">
          Crear nuevo curso
        </Button>
      </div>
    </DialogContent>
  </Dialog>
</template>