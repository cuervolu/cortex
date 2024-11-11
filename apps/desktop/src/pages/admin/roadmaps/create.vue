<script setup lang="ts">
import TurndownService from "turndown";
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import ContentForm from "~/components/education/ContentForm.vue";
import type { RoadmapCreateRequest } from "@cortex/shared/types";
import { useEducationalContent } from '~/composables/useEducationalContent';
import { useDesktopErrorHandler } from '~/composables/useDesktopErrorHandler';
import type { RoadmapFormValues, CourseModuleFormValues } from "~/composables";

const turndownService = new TurndownService({
  codeBlockStyle: 'fenced',
  headingStyle: 'atx'
});
const { createWithImage, isLoading } = useEducationalContent('roadmap');
const { handleError } = useDesktopErrorHandler();

const transformToRoadmapRequest = (formData: RoadmapFormValues): RoadmapCreateRequest => ({
  title: formData.title,
  description: turndownService.turndown(formData.description),
  tags: formData.tagNames.map(name => ({ name })),
  is_published: formData.is_published
});

const handleCreateRoadmap = async (
    formData: RoadmapFormValues | CourseModuleFormValues,
    imagePath: string | null
) => {
  if ('title' in formData) {
    try {
      const roadmapData = transformToRoadmapRequest(formData);
      await createWithImage(roadmapData, imagePath);
    } catch (e) {
      await handleError(e, {
        statusCode: 400,
        data: { formData, imagePath },
        silent: false
      });
    }
  } else {
    await handleError(new Error('Invalid form data type'), {
      statusCode: 400,
      data: { formData },
      silent: false
    });
  }
};
</script>

<template>
  <div class="p-8 w-full flex flex-col">
    <ContentForm
        content-type="roadmap"
        :icon-component="RoadmapIcon"
        submit-label="Crear Roadmap"
        :is-loading="isLoading"
        @submit="handleCreateRoadmap"
    />
  </div>
</template>