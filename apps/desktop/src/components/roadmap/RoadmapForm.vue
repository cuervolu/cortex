<script setup lang="ts">
import type {RoadmapCreateRequest, TagDTO} from "@cortex/shared/types";

const emit = defineEmits(['submit']);

const title = ref('');
const description = ref('');
const tags = ref<string[]>([]);
const selectedCourses = ref<number[]>([]);
const isPublished = ref(false);
const courses = ref<Array<{ id: number; name: string }>>([]);


const tagsDTO = computed((): TagDTO[] => {
  return tags.value.map(tag => ({name: tag}));
});

const form = computed((): RoadmapCreateRequest => {
  return {
    title: title.value,
    description: description.value,
    tags: tagsDTO.value,
    course_ids: selectedCourses.value,
    is_published: isPublished.value
  };
});

const handleSubmit = () => {
  emit('submit', form.value);
};

const handleEditorUpdate = (content: string) => {
  description.value = content;
};
</script>

<template>
  <form class="w-full max-w-4xl mx-auto p-6 space-y-6" @submit.prevent="handleSubmit">
    <div class="space-y-4">
      <!-- Título -->
      <div>
        <label class="block text-sm font-medium mb-2">Título</label>
        <Input v-model="title" placeholder="Ingrese el título del roadmap" required/>
      </div>

      <!-- Tags -->
      <div>
        <label class="block text-sm font-medium mb-2">Tags</label>
        <TagsInput v-model="tags">
          <TagsInputItem v-for="tag in tags" :key="tag" :value="tag">
            <TagsInputItemText/>
            <TagsInputItemDelete/>
          </TagsInputItem>
          <TagsInputInput placeholder="Agregar tag..."/>
        </TagsInput>
      </div>

      <!-- Cursos -->
      <div>
        <label class="block text-sm font-medium mb-2">Cursos</label>
        <Select v-model="selectedCourses" multiple>
          <SelectTrigger class="w-full">
            <SelectValue placeholder="Seleccionar cursos"/>
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Cursos Disponibles</SelectLabel>
              <SelectItem
                  v-for="course in courses"
                  :key="course.id"
                  :value="course.id"
              >
                {{ course.name }}
              </SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
      </div>

      <!-- Editor de Descripción -->
      <div>
        <label class="block text-sm font-medium mb-2">Descripción</label>
        <TiptapEditor
            :initial-content="description"
            @update:content="handleEditorUpdate"
        />
      </div>

      <!-- Publicar -->
      <div class="flex items-center space-x-2">
        <Switch v-model="isPublished"/>
        <label class="text-sm font-medium">Publicar roadmap</label>
      </div>

      <div class="pt-4">
        <Button type="submit" class="w-full">Crear Roadmap</Button>
      </div>
    </div>
  </form>
</template>