package com.cortex.backend.education.lesson.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.education.lesson.api.dto.LessonRequest;
import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import com.cortex.backend.education.lesson.api.dto.LessonUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/education/lesson")
@RequiredArgsConstructor
@Tag(name = "Lesson", description = "Endpoints for managing lessons")
public class LessonController {

  private final LessonService lessonService;

  @GetMapping
  @Operation(summary = "Get all published lessons", description = "Retrieves a list of all published lessons")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = LessonResponse.class)))
  public ResponseEntity<PageResponse<LessonResponse>> getAllPublishedLessons(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sort", required = false) String[] sort,
      Authentication authentication
  ) {
    Long userId = authentication != null ? ((User) authentication.getPrincipal()).getId() : null;
    return ResponseEntity.ok(lessonService.getAllPublishedLessons(page, size, sort, userId));
  }

  @GetMapping("/admin")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Get all lessons", description = "Retrieves a list of all lessons")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = LessonResponse.class)))
  public ResponseEntity<PageResponse<LessonResponse>> getAllLessons(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sort", required = false) String[] sort,
      Authentication authentication
  ) {
    Long userId = authentication != null ? ((User) authentication.getPrincipal()).getId() : null;
    return ResponseEntity.ok(lessonService.getAllLessons(page, size, sort, userId));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a lesson by ID", description = "Retrieves a lesson by its ID")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = LessonResponse.class)))
  @ApiResponse(responseCode = "404", description = "Lesson not found")
  public ResponseEntity<LessonResponse> getLessonById(
      @PathVariable Long id,
      Authentication authentication
  ) {
    Long userId = authentication != null ? ((User) authentication.getPrincipal()).getId() : null;
    return lessonService.getLessonById(id, userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/slug/{slug}")
  @Operation(summary = "Get a lesson by slug", description = "Retrieves a lesson by its slug")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = LessonResponse.class)))
  @ApiResponse(responseCode = "404", description = "Lesson not found")
  public ResponseEntity<LessonResponse> getLessonBySlug(
      @PathVariable String slug,
      Authentication authentication
  ) {
    Long userId = authentication != null ? ((User) authentication.getPrincipal()).getId() : null;
    return lessonService.getLessonBySlug(slug, userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }


  @PostMapping
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Create a new lesson", description = "Creates a new lesson")
  @ApiResponse(responseCode = "201", description = "Lesson created successfully",
      content = @Content(schema = @Schema(implementation = LessonResponse.class)))
  public ResponseEntity<LessonResponse> createLesson(@RequestBody @Valid LessonRequest request) {
    LessonResponse createdLesson = lessonService.createLesson(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdLesson);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Update a lesson", description = "Updates an existing lesson partially")
  @ApiResponse(responseCode = "200", description = "Lesson updated successfully",
      content = @Content(schema = @Schema(implementation = LessonResponse.class)))
  @ApiResponse(responseCode = "404", description = "Lesson not found")
  public ResponseEntity<LessonResponse> updateLesson(
      @PathVariable Long id,
      @RequestBody @Valid LessonUpdateRequest request) {
    LessonResponse updatedLesson = lessonService.updateLesson(id, request);
    return ResponseEntity.ok(updatedLesson);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Delete a lesson", description = "Deletes an existing lesson")
  @ApiResponse(responseCode = "204", description = "Lesson deleted successfully")
  @ApiResponse(responseCode = "404", description = "Lesson not found")
  public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
    lessonService.deleteLesson(id);
    return ResponseEntity.noContent().build();
  }
}