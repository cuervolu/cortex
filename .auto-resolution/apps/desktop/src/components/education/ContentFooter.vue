<script setup lang="ts">
interface Props {
  isPublished: boolean;
  isValid: boolean;
  isLoading?: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:isPublished', value: boolean): void;
}>();
</script>

<template>
  <div class="flex items-center justify-between pt-4 border-t">
    <div class="flex items-center space-x-2">
      <Switch
          :checked="isPublished"
          @update:checked="(value) => emit('update:isPublished', value)"
          class="peer"
          :disabled="isLoading"
      />
      <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
        Publicar contenido
      </label>
    </div>
    <Button
        type="submit"
        :disabled="!isValid || isLoading"
    >
      <slot>Guardar</slot>
    </Button>
  </div>
</template>