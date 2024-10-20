package com.cortex.backend.engine.internal;

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
public class LanguageConfig {

  private String name;
  private String dockerImage;
  private String executeCommand;
  private String compileCommand;
  private String fileExtension;
  private long memoryLimit;
  private long cpuLimit;
  private long timeout;
}