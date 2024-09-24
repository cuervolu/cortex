package com.cortex.backend.education.lesson.api.dto;

import jakarta.validation.constraints.Positive;
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
public class LessonUpdateRequest {

  private Long moduleId;

  private String name;

  private String content;

  @Positive(message = "Credits must be a positive number")
  private Integer credits;
}