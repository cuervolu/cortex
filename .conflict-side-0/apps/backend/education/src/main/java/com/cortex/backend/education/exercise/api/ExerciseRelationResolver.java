package com.cortex.backend.education.exercise.api;


public interface ExerciseRelationResolver {
  Long getLessonIdForExercise(Long exerciseId);
  boolean areAllExercisesCompletedForLesson(Long userId, Long lessonId);
}