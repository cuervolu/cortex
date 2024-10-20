import { invoke } from '@tauri-apps/api/core';
import type { ExerciseDetails } from '@cortex/shared/types';
import { ref, computed } from 'vue';
import { useRoute } from 'vue-router';

export function useExercise() {
  const route = useRoute();
  const exercise = ref<ExerciseDetails | null>(null);
  const currentLesson = ref('');
  const currentExercise = computed(() => exercise.value?.title || '');
  const currentFileName = ref('');
  const currentLanguage = ref('');
  const initialCode = ref('');
  const editorCode = ref('');

  const fetchExerciseDetails = async () => {
    const id = Number(route.params.id);
    try {
      const response = await invoke<ExerciseDetails>('get_exercise_details', { id });
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