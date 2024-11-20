<script setup lang="ts">
import ModelSelector from "~/components/ai/ModelSelector.vue";

interface AIModel {
  value: string
  label: string
  description: string
}

interface Props {
  modelValue?: string
  isOpen?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  isOpen: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'update:isOpen', value: boolean): void
  (e: 'change', model: AIModel): void
}>()

const handleModelChange = (model: AIModel) => {
  emit('change', model)
}

const handleOpenChange = (open: boolean) => {
  emit('update:isOpen', open)
}
</script>

<template>
  <Sheet :open="isOpen" @update:open="handleOpenChange">
    <SheetContent class="sm:max-w-xl w-full">
      <SheetHeader class="mb-6">
        <SheetTitle>Configuración de IA</SheetTitle>
        <SheetDescription>
          Personaliza tu experiencia de IA ajustando el modelo y sus configuraciones
        </SheetDescription>
      </SheetHeader>

      <div class="space-y-6">
        <ModelSelector
            :model-value="modelValue"
            @update:model-value="$emit('update:modelValue', $event)"
            @change="handleModelChange"
        />
      </div>
    </SheetContent>
  </Sheet>
</template>