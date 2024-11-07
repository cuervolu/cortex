package com.cortex.backend.education.course.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseResponse {
  private Long id;
  private String name;
  private String description;
  
  @JsonProperty("image_url")
  private String imageUrl;
  private String slug;
  
  @JsonProperty("roadmap_slugs")
  private Set<String> roadmapSlugs;
  
  @JsonProperty("tag_names")
  private List<String> tagNames;
  
  @JsonProperty("module_ids")
  private Set<Long> moduleIds;

  @JsonProperty("display_order")
  private Integer displayOrder;
  
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  
  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}