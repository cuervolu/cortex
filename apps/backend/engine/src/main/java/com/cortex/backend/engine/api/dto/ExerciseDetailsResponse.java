package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ExerciseDetailsResponse {

  private Long id;
  private String title;
  private String instructions;
  private String hints;

  @JsonProperty("initial_code")
  private String initialCode;

  @JsonProperty("test_code")
  private String testCode;

  @JsonProperty("lesson_name")
  private String lessonName;

  @JsonProperty("file_name")
  private String fileName;
  
  private String language;
}