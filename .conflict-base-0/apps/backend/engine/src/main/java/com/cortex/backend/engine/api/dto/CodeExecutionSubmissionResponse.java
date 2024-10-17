package com.cortex.backend.engine.api.dto;

import java.time.LocalDateTime;

public record CodeExecutionSubmissionResponse(
    String taskId,
    String status,
    String message,
    LocalDateTime submissionTime
) {}