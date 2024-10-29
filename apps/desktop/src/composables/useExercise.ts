import { invoke } from '@tauri-apps/api/core';
import { debug } from '@tauri-apps/plugin-log';
import type { ExerciseDetails } from '@cortex/shared/types';

export function useExercise() {
  const route = useRoute();
  const { handleError, createAppError } = useErrorHandler();

  const exercise = ref<ExerciseDetails | null>(null);
  const currentLesson = ref('');
  const currentExercise = computed(() => exercise.value?.title || '');
  const currentFileName = ref('');
  const currentLanguage = ref('');
  const initialCode = ref('');
  const editorCode = ref('');

  const fetchExerciseDetails = async () => {
    const id = Number(route.params.id);

    if (isNaN(id) || id <= 0) {
      throw await handleError(createAppError('Invalid exercise ID', {
        statusCode: 400,
        data: {
          id,
          params: route.params
        }
      }));
    }

    try {
      await debug(`Fetching exercise details for ID: ${id}`);
      const response = await invoke<ExerciseDetails>('get_exercise_details', { id });

      exercise.value = response;
      initialCode.value = response.initial_code;
      editorCode.value = response.initial_code;
      currentFileName.value = response.file_name;
      currentLanguage.value = response.language;
      currentLesson.value = response.lesson_name;

      await debug('Exercise details fetched successfully');

    } catch (error) {
      throw await handleError(error, {
        statusCode: 500,
        data: {
          action: 'fetch_exercise_details',
          exerciseId: id
        },
        fatal: true // This error is fatal and should be shown to the user
      });
    }
  };

  const handleCodeChange = (newCode: string) => {
    try {
      editorCode.value = newCode;
    } catch (error) {
      handleError(error, {
        statusCode: 400,
        data: {
          action: 'update_code',
          codeLength: newCode?.length
        }
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
    fetchExerciseDetails,
    handleCodeChange
  };
}