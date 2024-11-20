import { invoke } from '@tauri-apps/api/core';
import { debug, error } from '@tauri-apps/plugin-log';
import type { RoadmapModule } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';

export function useModules() {
    const module = ref<RoadmapModule | null>(null);
    const loading = ref(true);
    const errorHandler = useDesktopErrorHandler();

    const fetchModule = async (slug: string) => {
        try {
            loading.value = true;
            await debug(`Fetching module: ${slug}`);

            const response = await invoke<RoadmapModule>('get_module', { slug });

            if (!response) {
                throw new AppError('Module not found', {
                    statusCode: 404,
                    data: { slug },
                });
            }

            module.value = response;
            await debug(`Successfully fetched module: ${response.name}`);
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
        module,
        fetchModule,
        loading,
    };
}
