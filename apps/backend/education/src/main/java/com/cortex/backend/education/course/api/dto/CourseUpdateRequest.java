package com.cortex.backend.education.course.api.dto;

import com.cortex.backend.education.tags.api.dto.TagDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import java.util.Set;
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
public class CourseUpdateRequest {

  @Size(max = 255, message = "Name must not exceed 255 characters")
  private String name;

  private String description;

  private Set<TagDTO> tags;
  
  @JsonProperty("is_published")
  private Boolean isPublished;

  @JsonProperty("display_order")
  private Integer displayOrder;
}