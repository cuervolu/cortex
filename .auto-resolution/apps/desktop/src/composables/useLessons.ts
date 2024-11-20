import { invoke } from '@tauri-apps/api/core';
import { debug, error } from '@tauri-apps/plugin-log';
import type { Lesson } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';

export function useLessons() {
    const lesson = ref<Lesson | null>(null);
    const loading = ref(true);
    const errorHandler = useDesktopErrorHandler();

    const fetchLesson = async (slug: string) => {
        try {
            loading.value = true;
            await debug(`Fetching lesson: ${slug}`);

            const response = await invoke<Lesson>('get_lesson', { slug });

            if (!response) {
                throw new AppError('Lesson not found', {
                    statusCode: 404,
                    data: { slug },
                });
            }

            lesson.value = response;
            await debug(`Successfully fetched lesson: ${response.name}`);
            return response;
        } catch (err) {
            const isInvokeError = err instanceof Error && err.message.includes('invoke');
            await errorHandler.handleError(err, {
                statusCode: isInvokeError ? 500 : 404,
                fatal: isInvokeError,
                data: { slug },
            });
        } finally {
            loading.value = false;
        }
    };

    return {
        lesson,
        fetchLesson,
        loading,
    };
}
