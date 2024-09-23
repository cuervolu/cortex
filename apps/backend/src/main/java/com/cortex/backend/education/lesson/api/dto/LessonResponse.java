package com.cortex.backend.education.lesson.api.dto;

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
public class LessonResponse {
  private Long id;
  private String name;
  private String content;
  private Integer credits;
  private String slug;
  private Long moduleId;
  private String moduleName;
  private Set<Long> exerciseIds;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}