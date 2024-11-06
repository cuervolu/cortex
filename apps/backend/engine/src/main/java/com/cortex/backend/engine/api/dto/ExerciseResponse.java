package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
public class ExerciseResponse {
  private Long id;

  @JsonProperty("lesson_id")
  private Long lessonId;

  private String slug;

  private String title;

  @JsonProperty("github_path")
  private String githubPath;

  @JsonProperty("last_github_sync")
  private LocalDateTime lastGithubSync;

  private Integer points;

  private String instructions;

  private String hints;

  @JsonProperty("solution_responses")
  private Set<SolutionResponse> solutionResponses;

  @JsonProperty("display_order")
  private Integer displayOrder;
}
