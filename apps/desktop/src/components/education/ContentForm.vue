<script setup lang="ts">
import type { Component } from 'vue';
import {
  useImageDrop,
  useEducationalForm,
  type RoadmapFormValues,
  type CourseModuleFormValues
} from '~/composables';
import type { ContentType } from '~/types';
import ContentImageUpload from "~/components/education/ContentImageUpload.vue";
import ContentTitle from "~/components/education/ContentTitle.vue";
import ContentTags from "~/components/education/ContentTags.vue";
import ContentEditor from "~/components/education/ContentEditor.vue";
import ContentFooter from "~/components/education/ContentFooter.vue";
import type { Path } from "vee-validate";

interface Props {
  contentType: ContentType;
  iconComponent?: Component;
  submitLabel?: string;
  isLoading?: boolean;
}

const props = defineProps<Props>();

type FormValues<T extends ContentType> = T extends 'roadmap'
    ? RoadmapFormValues
    : CourseModuleFormValues;

const emit = defineEmits<{
  (e: 'submit', values: FormValues<typeof props.contentType>, imagePath: string | null): void;
}>();

const {
  previewImage,
  isDragging,
  setupDragListeners,
  handleImageUpload,
  cleanup,
  currentImagePath
} = useImageDrop();

const { form } = useEducationalForm(props.contentType);
const { values, meta } = form;

onMounted(setupDragListeners);
onUnmounted(cleanup);

const handleSubmit = form.handleSubmit((formValues) => {
  emit('submit', formValues as FormValues<typeof props.contentType>, currentImagePath.value);
});

const contentName = computed({
  get: () => props.contentType === 'roadmap'
      ? (values as RoadmapFormValues).title
      : (values as CourseModuleFormValues).name,
  set: (val: string) => {
    const field = props.contentType === 'roadmap' ? 'title' : 'name';
    form.setFieldValue(field as Path<typeof values>, val);
  }
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
        :is-loading="isLoading"
        @upload="handleImageUpload"
    />
    <div class="max-w-4xl mx-auto space-y-8">
      <ContentTitle
          v-model="contentName"
          :label="contentType === 'roadmap' ? 'Título' : 'Nombre'"
      />
      <ContentTags
          v-if="contentType !== 'module'"
          v-model="values.tagNames"
      />
      <ContentEditor
          v-model="values.description"
          @update="handleDescriptionUpdate"
      />
      <ContentFooter
          v-model:is-published="values.is_published"
          :is-valid="meta.valid"
          :is-loading="isLoading"
      >
        {{ submitLabel }}
      </ContentFooter>
    </div>
    <slot name="additional-content"/>
  </form>
</template>