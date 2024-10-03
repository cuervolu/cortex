package com.cortex.backend.education.roadmap.api.dto;

import com.cortex.backend.education.tags.api.dto.TagDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RoadmapUpdateRequest {

  @Size(max = 255, message = "Title must not exceed 255 characters")
  private String title;

  private String description;
  
  private Set<TagDTO> tags;

  @JsonProperty("course_ids")
  private Set<Long> courseIds;
}