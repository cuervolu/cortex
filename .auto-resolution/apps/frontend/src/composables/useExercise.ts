import type { ExerciseDetails } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';
import { API_ROUTES } from '@cortex/shared/config/api';
import { useWebErrorHandler } from '@cortex/shared/composables/front/useWebErrorHandler';

export function useExercise() {
  const route = useRoute();
  const errorHandler = useWebErrorHandler();
  const { token } = useAuth();

  const exercise = ref<ExerciseDetails | null>(null);
  const currentLesson = ref('');
  const currentExercise = computed(() => exercise.value?.title || '');
  const currentFileName = ref('');
  const currentLanguage = ref('');
  const initialCode = ref('');
  const editorCode = ref('');

  const isLoading = ref(false);

  const getFetchOptions = () => ({
    headers: {
      Authorization: `${token.value}`,
      'Content-Type': 'application/json',
    },
  });

  const fetchExerciseDetails = async () => {
    const id = Number(route.params.id);
    if (isNaN(id) || id <= 0) {
      await errorHandler.handleError(
        new AppError('Invalid exercise ID', {
          statusCode: 400,
          data: {
            id,
            params: route.params,
          },
        })
      );
      return;
    }

    try {
      isLoading.value = true;
      console.debug(`Fetching exercise details for ID: ${id}`);

      const response = await $fetch<ExerciseDetails>(
        `${API_ROUTES.EXERCISES}/${id}/details`,
        getFetchOptions()
      );

      if (!response) {
        throw new AppError('Exercise details not found', {
          statusCode: 404,
          data: { id },
        });
      }

      exercise.value = response;
      initialCode.value = response.initial_code;
      editorCode.value = response.initial_code;
      currentFileName.value = response.file_name;
      currentLanguage.value = response.language;
      currentLesson.value = response.lesson_name;

      console.debug('Exercise details fetched successfully');
    } catch (error) {
      await errorHandler.handleError(error, {
        statusCode: error instanceof AppError ? error.statusCode : 500,
        data: {
          action: 'fetch_exercise_details',
          exerciseId: id,
        },
        fatal: true,
      });
    } finally {
      isLoading.value = false;
    }
  };

  const handleCodeChange = (newCode: string) => {
    try {
      editorCode.value = newCode;
    } catch (error) {
      errorHandler.handleError(error, {
        statusCode: 400,
        data: {
          action: 'update_code',
          codeLength: newCode?.length,
        },
      });
    }
  };

  return {
    exercise,
    currentLesson,
    currentExercise,
    currentFileName,
    currentLanguage,
    initialCode,
    editorCode,
    isLoading,
    fetchExerciseDetails,
    handleCodeChange,
  };
}
