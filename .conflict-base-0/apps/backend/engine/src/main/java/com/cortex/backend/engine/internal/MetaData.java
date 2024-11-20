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
public class MetaData {
  private String name;
  private String language;
  private String category;
  @JsonProperty("completion_time")
  private String completionTime;
  @JsonProperty("review_status")
  private String reviewStatus;
}