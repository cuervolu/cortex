import { API_ROUTES } from "@cortex/shared/config/api";
import type { RoadmapEnrollment } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';

export function useRoadmapEnrollment() {
    const loading = ref(false);
    const error = ref<Error | null>(null);
    const errorHandler = useWebErrorHandler();
    const { token } = useAuth();

    const enrollInRoadmap = async (roadmapId: number) => {
        try {
            loading.value = true;
            error.value = null;

            const url = `${API_ROUTES.ROADMAPS}/${roadmapId}/enroll`;
            const response = await $fetch<RoadmapEnrollment>(url, {
                method: 'POST',
                headers: {
                    'Authorization': `${token.value}`,
                    'Content-Type': 'application/json',
                }
            });

            if (!response) {
                throw new AppError('Failed to enroll in roadmap', {
                    statusCode: 400,
                    data: { roadmapId }
                });
            }

            return response;
        } catch (err) {
            error.value = err as Error;
            await errorHandler.handleError(err, {
                statusCode: err instanceof AppError ? err.statusCode : 500,
                fatal: false,
                data: { roadmapId }
            });
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        enrollInRoadmap,
        loading,
        error
    };
}