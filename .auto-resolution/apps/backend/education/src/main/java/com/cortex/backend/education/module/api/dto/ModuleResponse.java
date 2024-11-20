package com.cortex.backend.education.module.api.dto;

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
public class ModuleResponse {

  private Long id;
  private String name;
  private String description;
  private String slug;
  
  @JsonProperty("course_id")
  private Long courseId;

  @JsonProperty("course_name")
  private String courseName;

  @JsonProperty("image_url")
  private String imageUrl;

  @JsonProperty("lesson_ids")
  private Set<Long> lessonIds;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

  @JsonProperty("display_order")
  private Integer displayOrder;

  @JsonProperty("is_published")
  private Boolean isPublished;
}