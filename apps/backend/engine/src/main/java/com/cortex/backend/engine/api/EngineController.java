package com.cortex.backend.engine.api;

import com.cortex.backend.core.common.exception.ResultNotAvailableException;
import com.cortex.backend.core.common.exception.UnsupportedLanguageException;
import com.cortex.backend.engine.api.dto.CodeExecutionRequest;
import com.cortex.backend.engine.api.dto.CodeExecutionResult;
import com.cortex.backend.engine.internal.services.CodeExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/engine")
@RequiredArgsConstructor
@Tag(name = "Engine", description = "Code Execution API")
public class EngineController {

  private final CodeExecutionService codeExecutionService;

  @PostMapping("/execute")
  @Operation(summary = "Submit code for execution",
      description = "Submits code for execution and returns a task ID")
  @ApiResponse(responseCode = "202", description = "Code submitted successfully",
      content = @Content(schema = @Schema(implementation = String.class)))
  @ApiResponse(responseCode = "400", description = "Invalid request")
  @ApiResponse(responseCode = "500", description = "Internal server error")
  public ResponseEntity<String> submitCodeExecution(
      @Valid @RequestBody CodeExecutionRequest request) {
    try {
      String taskId = codeExecutionService.submitCodeExecution(request);
      return ResponseEntity.accepted().body(taskId);
    } catch (UnsupportedLanguageException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
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