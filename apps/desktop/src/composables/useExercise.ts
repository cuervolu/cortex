import { invoke } from '@tauri-apps/api/core';
import type { ExerciseDetail } from '~/types';

export function useExercise() {
  const route = useRoute();
  const exercise = ref<ExerciseDetail | null>(null);
  const currentLesson = ref('');
  const currentExercise = computed(() => exercise.value?.title || '');
  const currentFileName = ref('exercise.py');
  const currentLanguage = ref('python');
  const initialCode = ref('');
  const editorCode = ref('');

  const fetchExerciseDetails = async () => {
    const id = Number(route.params.id);
    try {
      const response = await invoke<ExerciseDetail>('get_exercise_details', { id });
      exercise.value = response;
      initialCode.value = response.initial_code;
      editorCode.value = response.initial_code;
      currentFileName.value = response.file_name;
      currentLanguage.value = response.language;
      currentLesson.value = response.lesson_name;
    } catch (error) {
      console.error('Failed to fetch exercise details:', error);
    }
  };

  const handleCodeChange = (newCode: string) => {
    editorCode.value = newCode;
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