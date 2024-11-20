import {API_ROUTES} from "@cortex/shared/config/api";
import type {PaginatedCourses, CourseDetails} from "@cortex/shared/types"

interface CourseDetailsResponse {
    data: Ref<CourseDetails | null>
    error: Ref<string | null>
    isLoading: Ref<boolean>
    refresh: () => Promise<CourseDetailsResponse>
}

export const useCourses = () => {
    const error = ref<string | null>(null)
    const isLoading = ref(false)
    const courseDetailsData = ref<CourseDetails | null>(null)
    const { token } = useAuth()

    const getFetchOptions = () => ({
        headers: {
            'Authorization': `${token.value}`,
            'Content-Type': 'application/json',
        }
    })

    const getCourseDetails = async (slug: string): Promise<CourseDetailsResponse> => {
        isLoading.value = true
        error.value = null

        try {
            const url = `${API_ROUTES.CORUSES}/slug/${slug}`
            courseDetailsData.value = await $fetch<CourseDetails>(url, {
                ...getFetchOptions(),
            })

            return {
                data: courseDetailsData as Ref<CourseDetails | null>,
                error: readonly(error),
                isLoading: readonly(isLoading),
                refresh: () => getCourseDetails(slug)
            }
        } catch (e) {
            console.error('Error fetching course details:', e)
            error.value = e instanceof Error ? e.message : 'Error fetching course details'
            courseDetailsData.value = null
            return {
                data: courseDetailsData as Ref<CourseDetails | null>,
                error: readonly(error),
                isLoading: readonly(isLoading),
                refresh: () => getCourseDetails(slug)
            }
        } finally {
            isLoading.value = false
        }
    }

    return {
        getCourseDetails,
    }
}