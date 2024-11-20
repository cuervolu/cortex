package com.cortex.backend.engine.api;

import com.cortex.backend.engine.api.dto.CodeExecutionRequest;
import com.cortex.backend.engine.api.dto.CodeExecutionResult;
import com.cortex.backend.engine.api.dto.SubmissionResponse;

public interface SubmissionService {
  SubmissionResponse createSubmission(CodeExecutionRequest request, Long userId);
  void updateSubmissionWithResult(Long submissionId, CodeExecutionResult result);
  SubmissionResponse getSubmission(Long submissionId);
}