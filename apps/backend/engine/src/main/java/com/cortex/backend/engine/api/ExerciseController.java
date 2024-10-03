package com.cortex.backend.engine.api;

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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise", description = "Exercise management APIs")
public class ExerciseController {

  private final ExerciseService exerciseService;

  @PostMapping
  @Operation(summary = "Create a new exercise", description = "Creates a new exercise with the provided details")
  @ApiResponse(responseCode = "201", description = "Exercise created successfully",
      content = @Content(schema = @Schema(implementation = ExerciseResponse.class)))
  public ResponseEntity<ExerciseResponse> createExercise(@RequestBody CreateExercise createExerciseDTO) {
    ExerciseResponse createdExercise = exerciseService.createExercise(createExerciseDTO);
    return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
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
  @Operation(summary = "Get all exercises", description = "Retrieves a list of all exercises")
  @ApiResponse(responseCode = "200", description = "List of exercises retrieved successfully",
      content = @Content(schema = @Schema(implementation = ExerciseResponse.class)))
  public ResponseEntity<List<ExerciseResponse>> getAllExercises() {
    List<ExerciseResponse> exercises = exerciseService.getAllExercises();
    return ResponseEntity.ok(exercises);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete an exercise", description = "Deletes an exercise by its ID")
  @ApiResponse(responseCode = "204", description = "Exercise deleted successfully")
  public ResponseEntity<Void> deleteExercise(
      @Parameter(description = "ID of the exercise to delete") @PathVariable Long id) {
    exerciseService.deleteExercise(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/details")
  @Operation(summary = "Get exercise details", description = "Retrieves detailed information about an exercise")
  @ApiResponse(responseCode = "200", description = "Exercise details retrieved successfully",
      content = @Content(schema = @Schema(implementation = ExerciseDetailsResponse.class)))
  public ResponseEntity<ExerciseDetailsResponse> getExerciseDetails(
      @Parameter(description = "ID of the exercise to retrieve details for") @PathVariable Long id) {
    ExerciseDetailsResponse details = exerciseService.getExerciseDetails(id);
    return ResponseEntity.ok(details);
  }

  @GetMapping("/check-repository")
  @Operation(summary = "Check if exercise repository is empty", description = "Checks if the exercise repository is empty")
  @ApiResponse(responseCode = "200", description = "Repository status retrieved",
      content = @Content(schema = @Schema(implementation = Boolean.class)))
  public ResponseEntity<Boolean> isExerciseRepositoryEmpty() {
    boolean isEmpty = exerciseService.isExerciseRepositoryEmpty();
    return ResponseEntity.ok(isEmpty);
  }

  @GetMapping("/check-lessons")
  @Operation(summary = "Check if lessons are available", description = "Checks if lessons are available")
  @ApiResponse(responseCode = "200", description = "Lessons availability status retrieved",
      content = @Content(schema = @Schema(implementation = Boolean.class)))
  public ResponseEntity<Boolean> areLessonsAvailable() {
    boolean areAvailable = exerciseService.areLessonsAvailable();
    return ResponseEntity.ok(areAvailable);
  }
}