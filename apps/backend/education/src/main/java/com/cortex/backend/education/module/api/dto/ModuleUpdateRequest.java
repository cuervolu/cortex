package com.cortex.backend.education.module.api.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleUpdateRequest {

  private Long courseId;

  @Size(max = 255, message = "Name must not exceed 255 characters")
  private String name;

  private String description;
}