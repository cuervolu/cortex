package com.cortex.backend.engine.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.domain.ExerciseStatus;
import com.cortex.backend.engine.api.dto.BulkStatusUpdateRequest;
import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseDetailsResponse;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.ExerciseStats;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import com.cortex.backend.engine.internal.ExerciseConfig;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExerciseService {

  ExerciseResponse createExercise(CreateExercise createExerciseDTO);

  ExerciseResponse updateExercise(Long id, UpdateExercise updateExerciseDTO);

  Optional<ExerciseResponse> getExercise(Long id);

  Optional<ExerciseResponse> getExerciseBySlug(String slug);

  PageResponse<ExerciseResponse> getAllExercises(int page, int size);

  void deleteExercise(Long id);

  boolean isExerciseRepositoryEmpty();

  void updateOrCreateExercise(
      String exerciseName,
      String githubPath,
      String instructions,
      String hints,
      String slug,
      String language,
      ExerciseConfig config,
      Set<Long> prerequisites,
      Set<String> tags
  );

  boolean areLessonsAvailable();

  ExerciseDetailsResponse getExerciseDetails(Long id);

  PageResponse<ExerciseResponse> getExercisesByStatus(int page, int size, ExerciseStatus status);

  ExerciseResponse updateExerciseStatus(Long id, ExerciseStatus status, String reviewNotes);

  ExerciseResponse assignLessonToExercise(Long id, Long lessonId);

  void updateExerciseOrder(Long id, Integer newOrder);

  void deprecateExercise(Long id);

  //  void forceSyncExercises();
  ExerciseStats getExerciseStatistics();

  void bulkUpdateExerciseStatus(BulkStatusUpdateRequest request);

  PageResponse<ExerciseResponse> getAllExercises(int page, int size, ExerciseStatus status,
      String difficulty, List<String> tags);
}