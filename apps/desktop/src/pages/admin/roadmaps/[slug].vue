<script setup lang="ts">
import RoadmapIcon from '~/components/icons/RoadmapIcon.vue';
import ContentForm from '~/components/education/ContentForm.vue';
import type { RoadmapFormValues } from '~/composables';
import type {
  RoadmapCreateRequest,
  RoadmapUpdateRequest,
} from '@cortex/shared/types';
import { useToast } from '@cortex/shared/components/ui/toast';

const route = useRoute();
const router = useRouter();
const isCreateMode = computed(() => {
  const slug = route.params.slug as string;
  return !slug || slug === 'create';
});

const {
  roadmap,
  loading,
  fetchRoadmap,
  createRoadmap,
  updateRoadmap,
  transformFormToRequest,
} = useRoadmaps();

const { handleError } = useDesktopErrorHandler();

const isLoading = ref(true);
const error = ref<Error | null>(null);
const { toast } = useToast();

const loadRoadmap = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    if (!isCreateMode.value) {
      await fetchRoadmap(route.params.slug as string);
      if (!roadmap.value) {
        throw new Error('Roadmap no encontrado');
      }
    }
  } catch (e) {
    error.value = e as Error;
    await handleError(e, {
      statusCode: 404,
      data: { slug: route.params.slug },
      silent: false,
    });
    if (e instanceof Error && e.message.includes('not found')) {
      await router.push('/admin/roadmaps/create');
    }
  } finally {
    isLoading.value = isCreateMode.value ? false : loading.value;
  }
};

const handleSubmit = async (
  formData: RoadmapFormValues | CourseModuleFormValues,
  imagePath: string | null
) => {
  if (!('title' in formData)) {
    await handleError(new Error('Invalid form data type'), {
      statusCode: 400,
      data: { formData },
      silent: false,
    });
    return;
  }

  try {
    if (isCreateMode.value) {
      const request = transformFormToRequest<RoadmapCreateRequest>(
        formData,
        false
      );
      const result = await createRoadmap(request, imagePath);
      if (result) {
        toast({
          title: 'Roadmap creado',
          description: 'El roadmap ha sido creado correctamente',
        });
        await navigateTo(`/admin/roadmaps`);
      }
    } else if (roadmap.value) {
      const request = transformFormToRequest<RoadmapUpdateRequest>(
        formData,
        true
      );
      const updatedRoadmap = await updateRoadmap(
        roadmap.value.id,
        request,
        imagePath
      );

      if (updatedRoadmap) {
        toast({
          title: 'Roadmap actualizado',
          description: 'El roadmap ha sido actualizado correctamente',
        });
        await navigateTo(`/admin/roadmaps`);
      }
    }
  } catch (e) {
    await handleError(e, {
      statusCode: 400,
      data: { formData, imagePath },
      silent: false,
    });
  }
};
const initialValues = computed(() => {
  if (isCreateMode.value) return undefined;

  return roadmap.value
    ? {
        title: roadmap.value.title,
        description: roadmap.value.description,
        tagNames: roadmap.value.tag_names || [],
        is_published: roadmap.value.is_published,
        image_url: roadmap.value.image_url,
      }
    : undefined;
});

await loadRoadmap();
</script>

<template>
  <div class="p-8 w-full flex flex-col">
    <!-- Loading State -->
    <div v-if="isLoading" class="flex justify-center items-center h-64">
      <div
        class="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"
      />
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center p-4">
      <p class="text-destructive">{{ error.message }}</p>
      <Button class="mt-4" @click="router.push('/admin/roadmaps')">
        Volver al listado
      </Button>
    </div>

    <!-- Content Form -->
    <ContentForm
      content-type="roadmap"
      :icon-component="RoadmapIcon"
      :submit-label="isCreateMode ? 'Crear Roadmap' : 'Actualizar Roadmap'"
      :initial-values="initialValues"
      :is-loading="isCreateMode ? false : loading"
      @submit="handleSubmit"
    />
  </div>
</template>
