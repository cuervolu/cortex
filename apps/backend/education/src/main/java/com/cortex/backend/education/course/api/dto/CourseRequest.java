package com.cortex.backend.education.course.api.dto;

import com.cortex.backend.education.tags.api.dto.TagDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseRequest {

  @NotBlank(message = "Name is required")
  @Size(max = 255, message = "Name must not exceed 255 characters")
  private String name;

  @NotBlank(message = "Description is required")
  private String description;

  private Set<TagDTO> tags;
  
  @JsonProperty("is_published")
  private boolean isPublished;
}