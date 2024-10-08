package com.cortex.backend.education.lesson.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LessonRequest {

  @NotNull(message = "Module ID is required")
  @JsonProperty("module_id")
  private Long moduleId;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Content is required")
  private String content;

  @NotNull(message = "Credits are required")
  @Positive(message = "Credits must be a positive number")
  private Integer credits;
  
  @JsonProperty("is_published")
  private boolean isPublished;
}