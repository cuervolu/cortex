package com.cortex.backend.engine.api.dto;

import com.cortex.backend.core.domain.ExerciseDifficulty;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateExercise {
  @JsonProperty("lesson_id")
  private Long lessonId;

  @NotNull(message = "Title is required")
  @NotBlank(message = "Title must not be blank")
  private String title;

  @NotNull(message = "GitHub path is required")
  @NotBlank(message = "GitHub path must not be blank")
  @URL(message = "GitHub path must be a valid URL")
  @JsonProperty("github_path")
  private String githubPath;

  @NotNull(message = "Points are required")
  @Min(value = 0, message = "Points must be non-negative")
  private Integer points;

  @NotNull(message = "Instructions are required")
  @NotBlank(message = "Instructions must not be blank")
  private String instructions;

  private LocalDateTime lastGithubSync = LocalDateTime.now();
  private String hints;
  private Set<String> tags;
  private Set<Long> prerequisites;

  @JsonProperty("display_order")
  private Integer displayOrder;

  private ExerciseDifficulty difficulty;

  @JsonProperty("estimated_time_minutes")
  private Integer estimatedTimeMinutes;
}