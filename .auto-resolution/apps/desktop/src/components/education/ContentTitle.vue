<script setup lang="ts">
defineProps<{
  modelValue: string;
}>();

defineEmits<{
  (e: 'update:modelValue', value: string): void;
}>();
</script>

<template>
  <FormField v-slot="{ componentField }" name="title">
    <FormItem>
      <FormControl>
        <Input
            v-bind="componentField"
            :value="modelValue"
            type="text"
            placeholder="Título"
            class="w-full text-4xl font-bold bg-transparent border-none outline-none
                 placeholder:text-muted-foreground/50 focus:ring-0 border-0
                 focus-visible:ring-0 focus-visible:ring-offset-0"
            @update:model-value="$emit('update:modelValue', $event)"
        />
      </FormControl>
      <FormMessage />
    </FormItem>
  </FormField>
</template>