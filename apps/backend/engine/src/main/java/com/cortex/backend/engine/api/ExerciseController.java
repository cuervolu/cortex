package com.cortex.backend.engine.api;

import com.cortex.backend.engine.api.dto.ExerciseDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {

  private final ExerciseService exerciseService;

  @GetMapping("/{id}/details")
  public ResponseEntity<ExerciseDetailsResponse> getExerciseDetails(@PathVariable Long id) {
    ExerciseDetailsResponse details = exerciseService.getExerciseDetails(id);
    return ResponseEntity.ok(details);
  }
}