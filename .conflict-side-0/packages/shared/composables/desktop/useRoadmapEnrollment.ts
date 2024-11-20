import { invoke } from '@tauri-apps/api/core';
import { ref } from 'vue';
import { AppError, RoadmapEnrollment } from '@cortex/shared/types';
import { useDesktopErrorHandler } from './useDesktopErrorHandler';

export function useRoadmapEnrollment() {
    const loading = ref(false);
    const errorHandler = useDesktopErrorHandler();

    const enrollInRoadmap = async (roadmapId: number) => {
        try {
            loading.value = true;

            const response = await invoke<RoadmapEnrollment>('enroll_in_roadmap', { roadmapId });
            
            return response;
        } catch (err: any) {

            const errorData = err || {};
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
