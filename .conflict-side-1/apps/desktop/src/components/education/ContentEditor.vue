<script setup lang="ts">
import { useField } from 'vee-validate';

const props = defineProps<{
  modelValue: string;
  placeholder?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();

const { value, handleChange } = useField('description');

const handleUpdate = (content: string) => {
  handleChange(content);
  emit('update:modelValue', content);
};
</script>

<template>
  <FormField v-slot="{ componentField }" name="description">
    <FormItem>
      <FormControl>
        <TiptapEditor
            :initial-content="modelValue"
            :placeholder="placeholder"
            @update="handleUpdate"
        />
      </FormControl>
      <FormMessage />
    </FormItem>
  </FormField>
</template>