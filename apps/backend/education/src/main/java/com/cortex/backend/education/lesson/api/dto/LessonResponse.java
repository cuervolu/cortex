package com.cortex.backend.education.lesson.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("module_id")
  private Long moduleId;

  @JsonProperty("module_name")
  private String moduleName;

  @JsonProperty("exercise_ids")
  private Set<Long> exerciseIds;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}