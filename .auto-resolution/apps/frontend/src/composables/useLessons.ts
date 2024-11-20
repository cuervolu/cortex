import { API_ROUTES } from "@cortex/shared/config/api";
import type { Lesson } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';

export function useLessons() {
    const lesson = ref<Lesson | null>(null);
    const loading = ref(true);
    const errorHandler = useWebErrorHandler();
    const { token } = useAuth();

    const getFetchOptions = () => ({
        headers: {
            'Authorization': `${token.value}`,
            'Content-Type': 'application/json',
        }
    });

    const fetchLesson = async (slug: string) => {
        try {
            loading.value = true;
            console.log(`Fetching lesson: ${slug}`);

            const response = await $fetch<Lesson>(
                `${API_ROUTES.LESSONS}/slug/${slug}`,
                getFetchOptions()
            );

            if (!response) {
                throw new AppError('Lesson not found', {
                    statusCode: 404,
                    data: { slug }
                });
            }

            lesson.value = response;
            return response;
        } catch (err) {
            await errorHandler.handleError(err, {
                statusCode: err instanceof AppError ? err.statusCode : 500,
                fatal: false,
                data: { slug }
            });
        } finally {
            loading.value = false;
        }
    };

    return {
        lesson,
        fetchLesson,
        loading
    };
}
