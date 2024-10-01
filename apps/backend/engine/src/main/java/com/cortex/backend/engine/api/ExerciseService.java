package com.cortex.backend.engine.api;

import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseDetailsResponse;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import com.cortex.backend.engine.internal.ExerciseConfig;
import java.util.List;
import java.util.Optional;

public interface ExerciseService {
  ExerciseResponse createExercise(CreateExercise createExerciseDTO);
  ExerciseResponse updateExercise(Long id, UpdateExercise updateExerciseDTO);
  Optional<ExerciseResponse> getExercise(Long id);
  Optional<ExerciseResponse> getExerciseBySlug(String slug);
  List<ExerciseResponse> getAllExercises();
  void deleteExercise(Long id);
  boolean isExerciseRepositoryEmpty();
  void updateOrCreateExercise(String exerciseName, String githubPath, String instructions, String hints, String slug, String language, ExerciseConfig config);
  boolean areLessonsAvailable();
  ExerciseDetailsResponse getExerciseDetails(Long id);
}