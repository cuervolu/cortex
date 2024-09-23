package com.cortex.backend.education.course.api.dto;

import java.time.LocalDateTime;
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
  private String imageUrl;
  private String slug;
  private Set<String> roadmapSlugs;
  private Set<String> tagNames;
  private Set<Long> moduleIds;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}