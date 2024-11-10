<script setup lang="ts">

const props = defineProps<{
  isPublished: boolean;
  isValid: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:isPublished', value: boolean): void;
}>();

const isPublishedComputed = computed({
  get: () => props.isPublished,
  set: (value) => emit('update:isPublished', value)
});
</script>

<template>
  <div class="flex items-center justify-between pt-4 border-t">
    <div class="flex items-center space-x-2">
      <Switch
          v-model="isPublishedComputed"
          class="peer"
      />
      <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
        Publicar contenido
      </label>
    </div>

    <Button
        type="submit"
        :disabled="!isValid"
    >
      <slot>Guardar</slot>
    </Button>
  </div>
</template>