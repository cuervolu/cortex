<script setup lang="ts">
import { Play } from 'lucide-vue-next'

interface Props {
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<{
  (e: 'execute'): void
}>()
</script>

<template>
  <div class="absolute top-4 right-4 z-10">
    <Button
      variant="default"
      size="sm"
      :disabled="loading"
      class="shadow-lg"
      @click="emit('execute')"
    >
      <Play v-if="!loading" class="mr-2 h-4 w-4" />
      <span v-else class="animate-spin mr-2">⚪</span>
      {{ loading ? 'Ejecutando...' : 'Ejecutar Código' }}
    </Button>
  </div>
</template>
