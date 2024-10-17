package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
public class CodeExecutionResult  {

  private boolean success;
  
  private String stdout;
  
  private String stderr;
  
  @JsonProperty("execution_time")
  private int executionTime;
  
  private String language;

  @JsonProperty("exercise_id")
  private Long exerciseId;

  @JsonProperty("memory_used")
  private int memoryUsed; // in kilobytes

  @JsonProperty("test_case_results")
  private List<TestCaseResult> testCaseResults;
}