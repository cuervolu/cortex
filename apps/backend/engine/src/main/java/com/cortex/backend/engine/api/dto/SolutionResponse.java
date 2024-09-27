package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
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
public class SolutionResponse {
  private Long id;
  @JsonProperty("user_id")
  private Long userId;
  
  @JsonProperty("exercise_id")
  private Long exerciseId;
  private Long status;
  @JsonProperty("points_earned")
  private Integer pointsEarned;
  private Set<SubmissionResponse> submissions;
}
