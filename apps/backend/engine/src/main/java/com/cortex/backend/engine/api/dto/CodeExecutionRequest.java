package com.cortex.backend.engine.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CodeExecutionRequest {
  private String language;
  private String initialCode;
  private String userCode;
  private String testCode;
}