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
public class SubmissionResponse {

  private Long id;
  private String code;

  @JsonProperty("language_id")
  private Long languageId;
  private String stdin;

  @JsonProperty("expected_output")
  private String expectedOutput;

  @JsonProperty("solution_id")
  private Long solutionId;
}
