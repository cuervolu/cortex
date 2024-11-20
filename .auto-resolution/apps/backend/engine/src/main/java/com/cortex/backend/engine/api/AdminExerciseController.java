package com.cortex.backend.engine.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.domain.ExerciseStatus;
import com.cortex.backend.engine.api.dto.BulkStatusUpdateRequest;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.ExerciseStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/exercises")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Exercise", description = "Administrative exercise management APIs")
public class AdminExerciseController {

  private final ExerciseService exerciseService;

  @GetMapping("/pending")
  @Operation(summary = "Get pending exercises", description = "Retrieves exercises pending review")
  public ResponseEntity<PageResponse<ExerciseResponse>> getPendingExercises(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    PageResponse<ExerciseResponse> pendingExercises =
        exerciseService.getExercisesByStatus(page, size, ExerciseStatus.PENDING_REVIEW);
    return ResponseEntity.ok(pendingExercises);
  }

  @PatchMapping("/{id}/status")
  @Operation(summary = "Update exercise status", description = "Updates the status of an exercise")
  public ResponseEntity<ExerciseResponse> updateExerciseStatus(
      @PathVariable Long id,
      @RequestParam ExerciseStatus status,
      @RequestParam(required = false) String reviewNotes
  ) {
    ExerciseResponse exercise = exerciseService.updateExerciseStatus(id, status, reviewNotes);
    return ResponseEntity.ok(exercise);
  }

  @PatchMapping("/{id}/assign-lesson")
  @Operation(summary = "Assign lesson to exercise", description = "Assigns a lesson to a pending exercise")
  public ResponseEntity<ExerciseResponse> assignLesson(
      @PathVariable Long id,
      @RequestParam Long lessonId
  ) {
    ExerciseResponse exercise = exerciseService.assignLessonToExercise(id, lessonId);
    return ResponseEntity.ok(exercise);
  }

  @PatchMapping("/{id}/reorder")
  @Operation(summary = "Update exercise order", description = "Updates the display order of an exercise")
  public ResponseEntity<Void> updateOrder(
      @PathVariable Long id,
      @RequestParam Integer newOrder
  ) {
    exerciseService.updateExerciseOrder(id, newOrder);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete exercise", description = "Deletes an exercise (marks as DEPRECATED)")
  public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
    exerciseService.deprecateExercise(id);
    return ResponseEntity.noContent().build();
  }

//  @PostMapping("/sync")
//  @Operation(summary = "Force sync", description = "Forces a synchronization with the GitHub repository")
//  public ResponseEntity<String> forceSync() {
//    exerciseService.forceSyncExercises();
//    return ResponseEntity.ok("Sync started successfully");
//  }

  @GetMapping("/stats")
  @Operation(summary = "Get exercise statistics", description = "Retrieves statistics about exercises")
  public ResponseEntity<ExerciseStats> getExerciseStats() {
    ExerciseStats stats = exerciseService.getExerciseStatistics();
    return ResponseEntity.ok(stats);
  }

  @PatchMapping("/bulk-status")
  @Operation(summary = "Bulk update exercise status", description = "Updates the status of multiple exercises")
  public ResponseEntity<Void> bulkUpdateStatus(
      @RequestBody BulkStatusUpdateRequest request
  ) {
    exerciseService.bulkUpdateExerciseStatus(request);
    return ResponseEntity.ok().build();
  }
}