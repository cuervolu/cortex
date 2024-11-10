<script setup lang="ts">
import { useField } from 'vee-validate';

defineProps<{
  modelValue: string[];
}>();

defineEmits<{
  (e: 'update:modelValue', value: string[]): void;
}>();

const { value: tags } = useField<string[]>('tagNames');
</script>

<template>
  <FormField v-slot="{ field }" name="tagNames">
    <FormItem>
      <FormControl>
        <TagsInput
            :model-value="tags"
            @update:model-value="field.value"
        >
          <TagsInputItem v-for="tag in tags" :key="tag" :value="tag">
            <TagsInputItemText />
            <TagsInputItemDelete />
          </TagsInputItem>
          <TagsInputInput placeholder="Agregar tags..." />
        </TagsInput>
      </FormControl>
      <FormMessage />
    </FormItem>
  </FormField>
</template>