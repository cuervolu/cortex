package com.cortex.backend.education.module.api.dto;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleResponse {

  private Long id;
  private String name;
  private String description;
  private String slug;
  private Long courseId;
  private String courseName;
  private String imageUrl;
  private Set<Long> lessonIds;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}