<script setup lang="ts">
import { convertFileSrc } from '@tauri-apps/api/core';
import { open } from '@tauri-apps/plugin-dialog';
import { listen, type Event, TauriEvent } from '@tauri-apps/api/event';
import { debug } from "@tauri-apps/plugin-log";
import { ImagePlus } from 'lucide-vue-next';

import { AppError, type RoadmapCreateRequest, type TagDTO } from "@cortex/shared/types";

type DropEventPayload = {
  paths: string[];
  position: {
    x: number;
    y: number;
  };
};

const emit = defineEmits(['submit']);

const title = ref('');
const description = ref('');
const tags = ref<string[]>([]);
const selectedCourses = ref<number[]>([]);
const isPublished = ref(false);
const courses = ref<Array<{ id: number; name: string }>>([]);
const previewImage = ref<string | null>(null);
const isDragging = ref(false);

const unlisteners: (() => void)[] = [];

onMounted(async () => {
  try {
    // Drag Enter event
    const unlistenEnter = await listen(TauriEvent.DRAG_ENTER, async () => {
      await debug('File drag detected');
      isDragging.value = true;
    });
    unlisteners.push(unlistenEnter);

    // Drag Leave event
    const unlistenLeave = await listen(TauriEvent.DRAG_LEAVE, async () => {
      await debug('Drag leave detected');
      isDragging.value = false;
    });
    unlisteners.push(unlistenLeave);

    // Drop event
    const unlistenDrop = await listen<DropEventPayload>(
        TauriEvent.DRAG_DROP,
        async (event: Event<DropEventPayload>) => {
          await debug('DRAG_DROP event received');
          await debug(`Payload: ${JSON.stringify(event)}`);

          try {
            const filePaths = event.payload.paths;
            if (filePaths.length === 0) {
              await debug('No files dropped');
              return;
            }

            const filePath = filePaths[0];
            const lowercasePath = filePath.toLowerCase();

            if (lowercasePath.endsWith('.jpg') ||
                lowercasePath.endsWith('.jpeg') ||
                lowercasePath.endsWith('.png') ||
                lowercasePath.endsWith('.webp')) {
              previewImage.value = convertFileSrc(filePath);
              await debug(`Image loaded: ${filePath}`);
            } else {
              await debug('Invalid file type - not an image');
            }
          } catch (e) {
            await debug(`Error processing file: ${e}`);
          } finally {
            isDragging.value = false;
          }
        }
    );
    unlisteners.push(unlistenDrop);

    await debug('All event listeners are set up');
  } catch (e) {
    await debug(`Error setting up events: ${e}`);
  }
});

onUnmounted(() => {
  unlisteners.forEach(unlisten => unlisten());
});

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

const handleImageUpload = async (event?: MouseEvent) => {
  if (event) {
    event.preventDefault();
  }
  try {
    const selectedPath = await open({
      title: 'Select an image',
      directory: false,
      multiple: false,
      filters: [{name: 'Images', extensions: ['jpg', 'jpeg', 'png', 'webp']}]
    });

    if (selectedPath) {
      previewImage.value = convertFileSrc(selectedPath as string);
      await debug(`Selected image: ${selectedPath}`);
    }
  } catch (e) {
    throw new AppError('No image selected', {
      statusCode: 401,
      data: {e}
    });
  }
};

const handleEditorUpdate = (content: string) => {
  description.value = content;
};

const handleDragOver = (e: DragEvent) => {
  e.preventDefault();
  e.stopPropagation();
};

const handleDrop = (e: DragEvent) => {
  e.preventDefault();
  e.stopPropagation();
};
</script>

<template>
  <form
      class="w-full gap-5 mx-auto space-y-6 flex"
      @submit.prevent="handleSubmit"
      @dragover="handleDragOver"
      @drop="handleDrop"
  >
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
    <div class="flex flex-col max-w-2/5">
      <div class="flex flex-col gap-3">
        <Label class="block text-sm font-medium">Imagen Roadmap</Label>

        <!-- Upload Area -->
        <AspectRatio
            :ratio="16 / 9"
            class="cursor-pointer"
            @click="handleImageUpload"
        >
          <div
              class="w-full h-full rounded-md border-2 border-dashed transition-all duration-200 relative"
              :class="{
              'border-muted-foreground/50 hover:border-muted-foreground': !isDragging && !previewImage,
              'border-primary bg-primary/5': isDragging,
              'border-none': previewImage
            }"
          >
            <!-- Preview Image -->
            <img
                v-if="previewImage"
                :src="previewImage"
                alt="Roadmap preview"
                class="rounded-md object-cover w-full h-full"
            >

            <!-- Upload Placeholder -->
            <div
                v-else
                class="absolute inset-0 flex flex-col items-center justify-center gap-2 text-muted-foreground transition-colors"
                :class="{
                'text-primary': isDragging
              }"
            >
              <ImagePlus class="w-8 h-8" />
              <p class="text-sm font-medium text-center">
                {{ isDragging ? 'Suelta la imagen aquí' : 'Arrastra y suelta o da click para seleccionar una imagen' }}
              </p>
            </div>
          </div>
        </AspectRatio>
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