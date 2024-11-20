<script setup lang="ts">
import { Trash2 } from 'lucide-vue-next'

defineProps<{
  open: boolean
  loading?: boolean
}>()

defineEmits<{
  'update:open': [value: boolean]
  'confirm': []
}>()
</script>

<template>
  <Dialog :open="open" @update:open="$emit('update:open', $event)">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>¿Eliminar cuenta?</DialogTitle>
        <DialogDescription class="text-gray-600">
          Esta acción es irreversible. Se eliminarán todos tus datos y no podrás recuperar tu cuenta.
          ¿Estás seguro de que deseas continuar?
        </DialogDescription>
      </DialogHeader>
      <DialogFooter class="flex gap-4 sm:gap-0">
        <Button
            variant="ghost"
            @click="$emit('update:open', false)"
            :disabled="loading"
        >
          Cancelar
        </Button>
        <Button
            variant="destructive"
            @click="$emit('confirm')"
            :disabled="loading"
            class="flex items-center gap-2"
        >
          <Trash2 class="h-4 w-4" />
          <span>{{ loading ? 'Eliminando...' : 'Sí, eliminar cuenta' }}</span>
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>