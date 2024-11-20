import { API_ROUTES } from "@cortex/shared/config/api";
import type { RoadmapEnrollment } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';

export function useRoadmapEnrollment() {
    const loading = ref(false);
    const errorHandler = useWebErrorHandler();
    const { token } = useAuth();

    const getFetchOptions = () => ({
        method: 'POST',
        headers: {
            'Authorization': `${token.value}`,
            'Content-Type': 'application/json',
        }
    });

    const enrollInRoadmap = async (roadmapId: number) => {
        try {
            loading.value = true;

            const response = await $fetch<RoadmapEnrollment>(
                `${API_ROUTES.ROADMAPS}/${roadmapId}/enroll`, 
                getFetchOptions()
            );

            return response;
        } catch (err: any) {

            const errorData = err?.data || {};
            const code = errorData?.code;

            if (code === 328) {
                throw new AppError(errorData?.description || "User already enrolled", {
                    statusCode: code,
                });
            }

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
    };
}