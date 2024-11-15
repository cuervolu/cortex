package com.cortex.backend.engine.api;

import com.cortex.backend.core.common.exception.ResultNotAvailableException;
import com.cortex.backend.core.common.exception.UnsupportedLanguageException;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.engine.api.dto.CodeExecutionRequest;
import com.cortex.backend.engine.api.dto.CodeExecutionResult;
import com.cortex.backend.engine.api.dto.CodeExecutionSubmissionResponse;
import com.cortex.backend.engine.internal.services.CodeExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/engine")
@RequiredArgsConstructor
@Tag(name = "Engine", description = "Code Execution API")
@Slf4j
public class EngineController {

  private final CodeExecutionService codeExecutionService;

  @PostMapping("/execute")
  @Operation(summary = "Submit code for execution",
      description = "Submits code for execution and returns execution details")
  @ApiResponse(responseCode = "202", description = "Code submitted successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request or exercise state")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<CodeExecutionSubmissionResponse> submitCodeExecution(
      @Valid @RequestBody CodeExecutionRequest request,
      Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    String taskId = codeExecutionService.submitCodeExecution(request, user.getId());

    return ResponseEntity.accepted().body(new CodeExecutionSubmissionResponse(
        taskId,
        "SUBMITTED",
        "Code execution task submitted successfully",
        LocalDateTime.now()
    ));
  }

  @GetMapping("/result/{taskId}")
  @Operation(summary = "Get execution result",
      description = "Retrieves the result of a code execution task")
  @ApiResponse(responseCode = "200", description = "Result retrieved successfully",
      content = @Content(schema = @Schema(implementation = CodeExecutionResult.class)))
  @ApiResponse(responseCode = "404", description = "Result not found")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<CodeExecutionResult> getExecutionResult(
      @Parameter(description = "Task ID", required = true)
      @PathVariable String taskId) {
    try {
      CodeExecutionResult result = codeExecutionService.getExecutionResult(taskId);
      return ResponseEntity.ok(result);
    } catch (ResultNotAvailableException e) {
      return ResponseEntity.notFound().build();
    }
  }
}