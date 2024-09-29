package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
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
public class TestCaseResult implements Serializable {

  private boolean passed;
  private String input;
  @JsonProperty("expected_output")
  private String expectedOutput;
  
  @JsonProperty("actual_output")
  private String actualOutput;
  private String message;
}
