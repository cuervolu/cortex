package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestCaseResult {

  private boolean passed;
  private String input;
  @JsonProperty("expected_output")
  private String expectedOutput;
  
  @JsonProperty("actual_output")
  private String actualOutput;
  private String message;
}
