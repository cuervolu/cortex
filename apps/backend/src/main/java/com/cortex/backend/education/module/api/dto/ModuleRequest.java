package com.cortex.backend.education.module.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
public class ModuleRequest {
  @NotNull(message = "Course ID is required")
  private Long courseId;

  @NotBlank(message = "Name is required")
  @Size(max = 255, message = "Name must not exceed 255 characters")
  private String name;

  @NotBlank(message = "Description is required")
  private String description;

  private String imageAltText;
}