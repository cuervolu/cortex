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
import type {PartialDeep} from "type-fest";

interface Props {
  contentType: ContentType;
  iconComponent?: Component;
  submitLabel?: string;
  isLoading?: boolean;
  initialValues?: PartialDeep<RoadmapFormValues | CourseModuleFormValues>;
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
} = useImageDrop(props.initialValues?.image_url);

const { form } = useEducationalForm(
    props.contentType,
    {},
    props.initialValues
);
const { values, meta } = form;

onMounted(setupDragListeners);
onUnmounted(cleanup);

const handleSubmit = form.handleSubmit((formValues) => {
  const hasImageChanged = currentImagePath.value !== props.initialValues?.image_url;
  emit('submit', 
    formValues as FormValues<typeof props.contentType>, 
    hasImageChanged ? currentImagePath.value : null
  );
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

watch(() => props.initialValues, (newValues) => {
  if (newValues) {
    Object.entries(newValues).forEach(([key, value]) => {
      form.setFieldValue(key as Path<typeof values>, value);
    });
  }
}, { deep: true });
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
          @update:is-published="(value) => form.setFieldValue('is_published', value)"
      >
        {{ submitLabel }}
      </ContentFooter>
    </div>
    <slot name="additional-content"/>
  </form>
</template>