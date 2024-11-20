package com.cortex.backend.engine.api.dto;

import com.cortex.backend.core.domain.ExerciseDifficulty;
import com.cortex.backend.core.domain.ExerciseStatus;
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

  private ExerciseStatus status;
  private Set<String> tags;
  private Set<Long> prerequisites;
  private ExerciseDifficulty difficulty;

  @JsonProperty("estimated_time_minutes")
  private Integer estimatedTimeMinutes;

  @JsonProperty("pending_lesson_slug")
  private String pendingLessonSlug;

  @JsonProperty("pending_creator")
  private String pendingCreator;

  @JsonProperty("review_notes")
  private String reviewNotes;
}