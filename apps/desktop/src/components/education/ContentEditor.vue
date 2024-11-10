<script setup lang="ts">
defineProps<{
  modelValue: string;
  placeholder?: string;
}>();

defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();
</script>

<template>
  <FormField v-slot="{ componentField }" name="description">
    <FormItem>
      <FormControl>
        <TiptapEditor
            :initial-content="modelValue"
            :placeholder="placeholder"
            @update="$emit('update:modelValue', $event)"
        />
      </FormControl>
      <FormMessage />
    </FormItem>
  </FormField>
</template>