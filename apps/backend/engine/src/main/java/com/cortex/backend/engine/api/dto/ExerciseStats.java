package com.cortex.backend.engine.api.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseStats {
  private long totalExercises;
  private long publishedExercises;
  private long pendingReviewExercises;
  private long deprecatedExercises;
  private Map<String, Long> exercisesByDifficulty;
  private Map<String, Long> exercisesByTag;
  private double averagePoints;
  private double averageCompletionRate;
}