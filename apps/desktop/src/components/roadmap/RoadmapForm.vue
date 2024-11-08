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
  <form class="w-full gap-5 mx-auto space-y-6 flex" @submit.prevent="handleSubmit">
    <div class="w-full min-w-[300px] space-y-4">
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
    </div>

    <!-- Picture -->
    <div clas="flex flex-col max-w-2/5">
      <div class="flex flex-col gap-3">
        <label class="block text-sm font-medium">Imagen Roadmap</label>
        <!-- Image Preview -->
        <img 
          src="https://placehold.co/600x400" 
          alt="Roadmap preview" 
          class="w-full h-auto object-cover rounded-xl"
        >
        <div class="grid w-full max-w-sm items-center gap-1.5">
          <Label for="picture">Imagen</Label>
          <Input id="picture" type="file" />
        </div>
      </div>

      <div class="flex flex-col pt-4 gap-4">
        <!-- Publicar -->
        <div class="flex items-center space-x-2">
          <Switch v-model="isPublished"/>
          <label class="text-sm font-medium">Publicar roadmap</label>
        </div>

        <div>
          <Button type="submit" class="w-full">Crear Roadmap</Button>
        </div>
      </div>
    </div>
  </form>
</template>