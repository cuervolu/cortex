package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CodeExecutionRequest(
    String language,
    @JsonProperty("initial_code") String initialCode,
    @JsonProperty("user_code") String userCode,
    @JsonProperty("test_code") String testCode
) {}
