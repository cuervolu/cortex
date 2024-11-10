<script setup lang="ts">
import type { Component } from 'vue';
import { useImageDrop, useEducationalForm } from '~/composables';
import ContentImageUpload from "~/components/education/ContentImageUpload.vue";
import ContentTitle from "~/components/education/ContentTitle.vue";
import ContentTags from "~/components/education/ContentTags.vue";
import ContentEditor from "~/components/education/ContentEditor.vue";
import ContentFooter from "~/components/education/ContentFooter.vue";

interface Props {
  iconComponent?: Component;
  submitLabel?: string;
}

const props = withDefaults(defineProps<Props>(), {
  submitLabel: 'Guardar'
});

const emit = defineEmits<{
  (e: 'submit', values: never): void;
}>();

const { previewImage, isDragging, setupDragListeners, handleImageUpload, cleanup } = useImageDrop();
const { form, handleEditorUpdate } = useEducationalForm();

const { values, meta } = form;

onMounted(setupDragListeners);
onUnmounted(cleanup);

const handleSubmit = form.handleSubmit((formValues) => {
  emit('submit', formValues);
});

const handleDescriptionUpdate = (content: string) => {
  form.setFieldValue('description', content);
};
</script>


<template>
  <form
      class="w-full mx-auto space-y-6"
      @submit="handleSubmit"
  >
    <ContentImageUpload
        :preview-image="previewImage"
        :is-dragging="isDragging"
        :icon-component="iconComponent"
        @upload="handleImageUpload"
    />

    <div class="max-w-4xl mx-auto space-y-8">
      <ContentTitle v-model="values.title" />

      <ContentTags v-model="values.tagNames" />

      <ContentEditor
          v-model="values.description"
          @update="handleDescriptionUpdate"
      />

      <ContentFooter
          v-model:is-published="values.is_published"
          :is-valid="meta.valid"
      >
        {{ submitLabel }}
      </ContentFooter>
    </div>

    <slot name="additional-content" />
  </form>
</template>