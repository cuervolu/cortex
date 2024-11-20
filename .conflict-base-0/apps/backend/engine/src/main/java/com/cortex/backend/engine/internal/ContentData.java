package com.cortex.backend.engine.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentData {
  private int points;
  private String creator;
  @JsonProperty("lesson_slug")
  private String lessonSlug;
  @JsonProperty("display_order")
  private Integer displayOrder;
  private String difficulty;
  @JsonProperty("estimated_time")
  private Integer estimatedTimeMinutes;
}