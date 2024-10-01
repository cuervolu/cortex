package com.cortex.backend.engine.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseConfig {
  private int points;
  private String creator;

  @JsonProperty("lesson_id")
  private Long lessonId;
}