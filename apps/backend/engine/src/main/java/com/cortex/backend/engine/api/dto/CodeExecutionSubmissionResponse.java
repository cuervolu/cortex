package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record CodeExecutionSubmissionResponse(

    @JsonProperty("task_id")
    String taskId,
    
    String status,
    String message,

    @JsonProperty("submission_time")
    LocalDateTime submissionTime
) {

}