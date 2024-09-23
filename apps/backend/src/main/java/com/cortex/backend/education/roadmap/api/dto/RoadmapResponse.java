package com.cortex.backend.education.roadmap.api.dto;

import java.time.LocalDateTime;
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
  private String imageUrl;
  private String slug;
  private Set<String> tagNames;
  private Set<String> courseSlugs;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}