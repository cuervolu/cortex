package com.cortex.backend.education.module.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ModuleRequest {
  @NotNull(message = "Course ID is required")
  @JsonProperty("course_id")
  private Long courseId;

  @NotBlank(message = "Name is required")
  @Size(max = 255, message = "Name must not exceed 255 characters")
  private String name;

  @NotBlank(message = "Description is required")
  private String description;
  
  @JsonProperty("is_published")
  private boolean isPublished;
}