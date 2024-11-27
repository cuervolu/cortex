<script setup lang="ts">
import { BookOpen } from 'lucide-vue-next'
import ContentForm from '~/components/education/ContentForm.vue'
import type { CourseModuleFormValues } from '~/composables'

defineProps<{
  roadmapId?: number
  isLoading?: boolean
}>()

const emit = defineEmits<{
  (e: 'submit', values: CourseModuleFormValues, imagePath: string | null): void
}>()

const handleFormSubmit = (values: CourseModuleFormValues, imagePath: string | null) => {
  emit('submit', {
    ...values,
    name: values.name || values.title
  }, imagePath)
}
</script>

<template>
  <ContentForm
      content-type="course"
      :icon-component="BookOpen"
      submit-label="Crear Curso"
      :is-loading="isLoading"
      @submit="handleFormSubmit"
  />
</template>