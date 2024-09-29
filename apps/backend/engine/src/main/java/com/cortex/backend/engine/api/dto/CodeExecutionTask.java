package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CodeExecutionTask(
    @JsonProperty("task_id")
    String taskId,
    CodeExecutionRequest request,
    @JsonProperty("content_hash") String contentHash,
    @JsonProperty("github_path") String githubPath
) {

}
