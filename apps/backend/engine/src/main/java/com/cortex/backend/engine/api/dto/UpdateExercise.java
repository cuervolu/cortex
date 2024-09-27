package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class UpdateExercise {

  @NotNull(message = "Lesson ID is required")
  @JsonProperty("lesson_id")
  private Long lessonId;
  
  @NotBlank(message = "Title must not be blank")
  private String title;

  @NotBlank(message = "GitHub path must not be blank")
  @JsonProperty("github_path")
  @URL(message = "GitHub path must be a valid URL")
  private String githubPath;

  @Min(value = 0, message = "Points must be non-negative")
  private Integer points;

  @NotBlank(message = "Instructions must not be blank")
  private String instructions;

  private String hints;
}