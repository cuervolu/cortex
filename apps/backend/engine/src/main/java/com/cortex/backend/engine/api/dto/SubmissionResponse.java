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

  @JsonProperty("cpu_time_limit")
  private Float cpuTimeLimit;

  @JsonProperty("cpu_extra_time")
  private Float cpuExtraTime;

  @JsonProperty("command_line_arguments")
  private String commandLineArguments;

  @JsonProperty("compiler_options")
  private String compilerOptions;

  @JsonProperty("solution_id")
  private Long solutionId;
}
