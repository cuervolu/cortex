<script setup lang="ts">
import {BookOpen} from 'lucide-vue-next'
import ContentForm from '~/components/education/ContentForm.vue'
import type {CourseModuleFormValues} from '~/composables'
import {useToast} from '@cortex/shared/components/ui/toast'

const route = useRoute()
const router = useRouter()
const courseId = computed(() => Number(route.query.courseId))
const isCreateMode = computed(() => !route.params.slug || route.params.slug === 'create')

const {
    module,
    loading,
    createModule,
    updateModule,
    fetchModule,
    fetchModuleById,
    transformFormToRequest
} = useModules()

const {handleError} = useDesktopErrorHandler()
const {toast} = useToast()
const isLoading = ref(true)
const error = ref<Error | null>(null)

const loadModule = async () => {
    isLoading.value = true;
    error.value = null;

    try {
        if (!isCreateMode.value) {
        // Try to load by ID first if we have one
        const id = parseInt(route.params.slug as string);
        if (!isNaN(id)) {
            await fetchModuleById(id);
        } else {
            await fetchModule(route.params.slug as string);
        }

        if (!module.value) {
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
        await router.push('/admin/modules/create');
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
        const result = await createModule(request, imagePath, courseId.value);
        if (result) {
            toast({
            title: 'Modulo creado',
            description: 'El modulo ha sido creado y asignado correctamente'
            })
            await router.push(`/admin/courses/${courseId.value}/modules`)
        }
        } else if (module.value) {
        const result = await updateModule(module.value.id, request, imagePath)
        if (result) {
            toast({
            title: 'Modulo actualizado',
            description: 'El module ha sido actualizado correctamente'
            })
            await router.push('/admin/modules')
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

    return module.value ? {
        title: module.value.name,
        name: module.value.name,
        description: module.value.description,
        is_published: module.value.is_published,
        image_url: module.value.image_url,
    } : undefined
})

await loadModule()
</script>

<template>
    <div class="p-8 w-full flex flex-col">
        <div v-if="isLoading" class="flex justify-center items-center h-64">
        <div class="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"/>
        </div>

        <div v-else-if="error" class="text-center p-4">
        <p class="text-destructive">{{ error.message }}</p>
        <Button class="mt-4" @click="router.push('/admin/modules')">
            Volver al listado
        </Button>
        </div>

        <ContentForm
            v-else
            content-type="module"
            :id="courseId"
            :icon-component="BookOpen"
            :submit-label="isCreateMode ? 'Crear Modulo' : 'Actualizar Modulo'"
            :initial-values="initialValues"
            :is-loading="isCreateMode ? false : loading"
            @submit="handleSubmit"
        />
    </div>
</template>