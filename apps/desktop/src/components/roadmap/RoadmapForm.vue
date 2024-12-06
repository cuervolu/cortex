<script setup lang="ts">
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import ContentForm from "~/components/education/ContentForm.vue";
import type {RoadmapFormValues} from "~/composables";

defineProps<{
  isLoading?: boolean
}>();

const emit = defineEmits<{
  (e: 'submit', values: RoadmapFormValues, imagePath: string | null): void
}>();

const handleFormSubmit = (values: RoadmapFormValues, imagePath: string | null) => {
  emit('submit', {
    ...values,
    name: values.name || values.title
  }, imagePath);
};
</script>

<template>
  <ContentForm
      content-type="roadmap"
      :icon-component="RoadmapIcon"
      submit-label="Crear Roadmap"
      :is-loading="isLoading"
      @submit="handleFormSubmit"
  />
</template>