<script setup lang="ts">
import {convertFileSrc} from '@tauri-apps/api/core';
import {open} from '@tauri-apps/plugin-dialog';
import {listen, type Event, TauriEvent} from '@tauri-apps/api/event';
import {debug} from "@tauri-apps/plugin-log";
import {ImagePlus} from 'lucide-vue-next';
import {AppError, type RoadmapCreateRequest, type TagDTO} from "@cortex/shared/types";
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";

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
const isPublished = ref(false);
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
    course_ids: undefined,
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
      class="w-full mx-auto space-y-6"
      @submit.prevent="handleSubmit"
      @dragover="handleDragOver"
      @drop="handleDrop"
  >
    <!-- Hero Image Section -->
    <div
        class="w-full h-48 relative overflow-hidden rounded-lg group cursor-pointer transition-all"
        @click="handleImageUpload"
    >
      <!-- Preview Image or Placeholder -->
      <div
          class="w-full h-full rounded-md border-2 border-dashed transition-all duration-200"
          :class="{
          'border-muted-foreground/50 hover:border-muted-foreground': !isDragging && !previewImage,
          'border-primary bg-primary/5': isDragging,
          'border-none': previewImage
        }"
      >
        <img
            v-if="previewImage"
            :src="previewImage"
            alt="Roadmap cover"
            class="w-full h-full object-cover"
        >
        <div
            v-else
            class="absolute inset-0 flex flex-col items-center justify-center gap-2 text-muted-foreground"
            :class="{ 'text-primary': isDragging }"
        >
          <ImagePlus class="w-8 h-8"/>
          <p class="text-sm font-medium text-center">
            {{
              isDragging ? 'Suelta la imagen aquí' : 'Arrastra y suelta o da click para agregar una portada'
            }}
          </p>
        </div>
      </div>

      <!-- Floating Edit Button - Appears on Hover when image exists -->
      <div
          v-if="previewImage"
          class="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity"
      >
        <Button
            variant="secondary"
            size="sm"
            class="absolute right-4 top-4"
            @click.stop="handleImageUpload"
        >
          <ImagePlus class="w-4 h-4 mr-2"/>
          Cambiar portada
        </Button>
      </div>

      <!-- Fixed Icon Badge -->
      <div class="absolute left-8 bottom-8">
        <div
            class="w-12 h-12 bg-white/90 backdrop-blur-sm rounded-lg shadow-lg p-2 transition-all duration-200">
          <RoadmapIcon class="w-8 h-8" fill="#28282B"/>
        </div>
      </div>
    </div>


    <div class="max-w-4xl mx-auto space-y-8">
      <!-- Title - Notion Style -->
      <Input
          v-model="title"
          type="text"
          placeholder="Título del Roadmap"
          class="w-full text-4xl font-bold bg-transparent border-none outline-none placeholder:text-muted-foreground/50
          focus:ring-0 border-0 focus-visible:ring-0 focus-visible:ring-offset-0"
          required
      />

      <!-- Tags Section -->
      <div class="space-y-2">
        <TagsInput v-model="tags">
          <TagsInputItem v-for="tag in tags" :key="tag" :value="tag">
            <TagsInputItemText/>
            <TagsInputItemDelete/>
          </TagsInputItem>
          <TagsInputInput placeholder="Agregar tags..."/>
        </TagsInput>
      </div>
      <!-- Description Editor -->
      <div class="min-h-[200px] w-full">
        <TiptapEditor
            :initial-content="description"
            @update:content="handleEditorUpdate"
        />
      </div>

      <!-- Footer Actions -->
      <div class="flex items-center justify-between pt-4 border-t">
        <div class="flex items-center space-x-2">
          <Switch v-model="isPublished"/>
          <label
              class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
            Publicar roadmap
          </label>
        </div>

        <Button type="submit">Crear Roadmap</Button>
      </div>
    </div>
  </form>
</template>