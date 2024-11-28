<script setup lang="ts">
import {BookOpen} from 'lucide-vue-next'
import ContentForm from '~/components/education/ContentForm.vue'
import type {CourseModuleFormValues} from '~/composables'
import {useToast} from '@cortex/shared/components/ui/toast'

const route = useRoute()
const router = useRouter()
const roadmapId = computed(() => Number(route.query.roadmapId))
const isCreateMode = computed(() => !route.params.slug || route.params.slug === 'create')

const {
  course,
  loading,
  createCourse,
  updateCourse,
  fetchCourse,
    fetchCourseById,
  transformFormToRequest
} = useCourses()

const {handleError} = useDesktopErrorHandler()
const {toast} = useToast()
const isLoading = ref(true)
const error = ref<Error | null>(null)

const loadCourse = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    if (!isCreateMode.value) {
      // Try to load by ID first if we have one
      const id = parseInt(route.params.slug as string);
      if (!isNaN(id)) {
        await fetchCourseById(id);
      } else {
        await fetchCourse(route.params.slug as string);
      }

      if (!course.value) {
        throw new Error('Curso no encontrado');
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
      await router.push('/admin/courses/create');
    }
  } finally {
    isLoading.value = false;
  }
};

const handleSubmit = async (formData: CourseModuleFormValues, imagePath: string | null) => {
  try {
    // Ensure name is set from title if not present
    const enrichedFormData = {
      ...formData,
      name: formData.name || formData.title,
      display_order: 0
    };

    const request = transformFormToRequest(enrichedFormData);

    if (isCreateMode.value) {
      const result = await createCourse(request, imagePath, roadmapId.value);
      if (result) {
        toast({
          title: 'Curso creado',
          description: 'El curso ha sido creado y asignado correctamente'
        })
        await router.push(`/admin/roadmaps/${roadmapId.value}/courses`)
      }
    } else if (course.value) {
      const result = await updateCourse(course.value.id, request, imagePath)
      if (result) {
        toast({
          title: 'Curso actualizado',
          description: 'El curso ha sido actualizado correctamente'
        })
        await router.push('/admin/courses')
      }
    }
  } catch (e) {
    await handleError(e, {
      statusCode: 400,
      data: {formData, imagePath},
      silent: false
    })
  }
}

const initialValues = computed(() => {
  if (isCreateMode.value) return undefined

  return course.value ? {
    title: course.value.name,
    name: course.value.name,
    description: course.value.description,
    tagNames: course.value.tag_names || [],
    is_published: course.value.is_published,
    image_url: course.value.image_url,
  } : undefined
})

await loadCourse()
</script>

<template>
  <div class="p-8 w-full flex flex-col">
    <div v-if="isLoading" class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"/>
    </div>

    <div v-else-if="error" class="text-center p-4">
      <p class="text-destructive">{{ error.message }}</p>
      <Button class="mt-4" @click="router.push('/admin/courses')">
        Volver al listado
      </Button>
    </div>

    <ContentForm
        v-else
        content-type="course"
        :icon-component="BookOpen"
        :submit-label="isCreateMode ? 'Crear Curso' : 'Actualizar Curso'"
        :initial-values="initialValues"
        :is-loading="loading"
        @submit="handleSubmit"
    />
  </div>
</template>