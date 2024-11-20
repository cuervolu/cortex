package com.cortex.backend.engine.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.domain.ExerciseStatus;
import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseDetailsResponse;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise", description = "Exercise management APIs")
public class ExerciseController {

  private final ExerciseService exerciseService;

  @PostMapping
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  @Operation(summary = "Create a new exercise", description = "Creates a new exercise with the provided details")
  @ApiResponse(responseCode = "201", description = "Exercise created successfully",
      content = @Content(schema = @Schema(implementation = ExerciseResponse.class)))
  public ResponseEntity<ExerciseResponse> createExercise(@RequestBody CreateExercise createExerciseDTO) {
    ExerciseResponse createdExercise = exerciseService.createExercise(createExerciseDTO);
    return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  @Operation(summary = "Update an existing exercise", description = "Updates an existing exercise with the provided details")
  @ApiResponse(responseCode = "200", description = "Exercise updated successfully",
      content = @Content(schema = @Schema(implementation = ExerciseResponse.class)))
  public ResponseEntity<ExerciseResponse> updateExercise(
      @Parameter(description = "ID of the exercise to update") @PathVariable Long id,
      @RequestBody UpdateExercise updateExerciseDTO) {
    ExerciseResponse updatedExercise = exerciseService.updateExercise(id, updateExerciseDTO);
    return ResponseEntity.ok(updatedExercise);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get an exercise by ID", description = "Retrieves an exercise by its ID")
  @ApiResponse(responseCode = "200", description = "Exercise found",
      content = @Content(schema = @Schema(implementation = ExerciseResponse.class)))
  @ApiResponse(responseCode = "404", description = "Exercise not found")
  public ResponseEntity<ExerciseResponse> getExercise(
      @Parameter(description = "ID of the exercise to retrieve") @PathVariable Long id) {
    return exerciseService.getExercise(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/slug/{slug}")
  @Operation(summary = "Get an exercise by slug", description = "Retrieves an exercise by its slug")
  @ApiResponse(responseCode = "200", description = "Exercise found",
      content = @Content(schema = @Schema(implementation = ExerciseResponse.class)))
  @ApiResponse(responseCode = "404", description = "Exercise not found")
  public ResponseEntity<ExerciseResponse> getExerciseBySlug(
      @Parameter(description = "Slug of the exercise to retrieve") @PathVariable String slug) {
    return exerciseService.getExerciseBySlug(slug)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  @Operation(summary = "Get all published exercises", description = "Retrieves a paginated list of published exercises")
  public ResponseEntity<PageResponse<ExerciseResponse>> getAllExercises(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) ExerciseStatus status,
      @RequestParam(required = false) String difficulty,
      @RequestParam(required = false) List<String> tags
  ) {
    PageResponse<ExerciseResponse> exercises = exerciseService.getAllExercises(page, size, status, difficulty, tags);
    return ResponseEntity.ok(exercises);
  }

  @GetMapping("/{id}/details")
  @Operation(summary = "Get exercise details", description = "Retrieves detailed information about an exercise")
  public ResponseEntity<ExerciseDetailsResponse> getExerciseDetails(@PathVariable Long id) {
    ExerciseDetailsResponse details = exerciseService.getExerciseDetails(id);
    return ResponseEntity.ok(details);
  }
}