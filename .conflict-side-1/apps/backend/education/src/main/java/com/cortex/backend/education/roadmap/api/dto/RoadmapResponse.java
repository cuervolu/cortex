package com.cortex.backend.education.roadmap.api.dto;

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
public class RoadmapResponse {

  private Long id;
  private String title;
  private String description;
  @JsonProperty("image_url")
  private String imageUrl;
  private String slug;
  @JsonProperty("tag_names")
  private List<String> tagNames;
  @JsonProperty("is_published")
  private boolean isPublished;
  @JsonProperty("course_slugs")
  private Set<String> courseSlugs;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}