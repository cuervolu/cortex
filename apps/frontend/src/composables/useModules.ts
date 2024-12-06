import { useWebErrorHandler } from "@cortex/shared/composables/front/useWebErrorHandler";
import { API_ROUTES } from "@cortex/shared/config/api";
import type { RoadmapModule } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';

export function useModules() {
    const module = ref<RoadmapModule | null>(null);
    const loading = ref(true);
    const errorHandler = useWebErrorHandler();
    const { token } = useAuth();

    const getFetchOptions = () => ({
        headers: {
            'Authorization': `${token.value}`,
            'Content-Type': 'application/json',
        }
    });

    const fetchModule = async (slug: string) => {
        try {
            loading.value = true;
            console.log(`Fetching module: ${slug}`);

            const response = await $fetch<RoadmapModule>(
                `${API_ROUTES.MODULES}/slug/${slug}`,
                getFetchOptions()
            );

            if (!response) {
                throw new AppError('Module not found', {
                    statusCode: 404,
                    data: { slug }
                });
            }

            module.value = response;
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
        module,
        fetchModule,
        loading
    };
}
