<script setup lang="ts">
import type { CourseModuleFormValues } from '~/composables'
import { useToast } from '@cortex/shared/components/ui/toast'
import CourseForm from "~/components/courses/CourseForm.vue";
import {debug} from "@tauri-apps/plugin-log";

const route = useRoute()
const router = useRouter()
const roadmapId = computed(() => Number(route.query.roadmapId))

const {
  loading,
  createCourse,
  transformFormToRequest
} = useCourses()

const { handleError } = useDesktopErrorHandler()
const { toast } = useToast()

const handleSubmit = async (formData: CourseModuleFormValues, imagePath: string | null) => {
  try {
    await debug(`Creating course with data: ${JSON.stringify(formData)}`)
    const request = transformFormToRequest({
      ...formData,
      display_order: 0
    })

    const result = await createCourse(request, imagePath)

    if (result) {
      toast({
        title: 'Curso creado',
        description: 'El curso ha sido creado correctamente'
      })

      if (roadmapId.value) {
        await router.push(`/admin/roadmaps/${roadmapId.value}/courses`)
      } else {
        await router.push('/admin/courses')
      }
    }
  } catch (e) {
    await handleError(e, {
      statusCode: 400,
      data: { formData, imagePath },
      silent: false
    })
  }
}
</script>

<template>
  <div class="p-8 w-full flex flex-col">
    <CourseForm
        :roadmap-id="roadmapId"
        :is-loading="loading"
        @submit="handleSubmit"
    />
  </div>
</template>