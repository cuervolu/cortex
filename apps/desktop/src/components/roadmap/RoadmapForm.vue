<script setup lang="ts">
import type { RoadmapCreateRequest } from "@cortex/shared/types";
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import ContentForm from "~/components/education/ContentForm.vue";

defineEmits<{
  (e: 'submit', form: RoadmapCreateRequest): void;
}>();
</script>

<template>
  <ContentForm
      :icon-component="RoadmapIcon"
      submit-label="Crear Roadmap"
      @submit="$emit('submit', $event)"
  />
</template>