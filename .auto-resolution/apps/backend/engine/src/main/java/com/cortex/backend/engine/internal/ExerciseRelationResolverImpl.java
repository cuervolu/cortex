package com.cortex.backend.engine.internal;

import com.cortex.backend.education.exercise.api.ExerciseRelationResolver;
import com.cortex.backend.engine.api.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExerciseRelationResolverImpl implements ExerciseRelationResolver {
  private final ExerciseRepository exerciseRepository;

  @Override
  @Transactional(readOnly = true)
  public Long getLessonIdForExercise(Long exerciseId) {
    return exerciseRepository.findById(exerciseId)
        .map(exercise -> exercise.getLesson().getId())
        .orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean areAllExercisesCompletedForLesson(Long userId, Long lessonId) {
    return exerciseRepository.areAllExercisesCompletedForLesson(userId, lessonId);
  }
}