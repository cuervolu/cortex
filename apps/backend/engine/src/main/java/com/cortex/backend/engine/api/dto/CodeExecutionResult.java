package com.cortex.backend.engine.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CodeExecutionResult {
  private boolean success;
  private String output;
  private String errorMessage;
}