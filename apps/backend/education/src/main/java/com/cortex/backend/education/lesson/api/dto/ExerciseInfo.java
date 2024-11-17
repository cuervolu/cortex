package com.cortex.backend.education.lesson.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseInfo {
  private Long id;
  private String slug;
  private String title;
  @JsonProperty("is_completed")
  private boolean isCompleted;
  private int points;
}