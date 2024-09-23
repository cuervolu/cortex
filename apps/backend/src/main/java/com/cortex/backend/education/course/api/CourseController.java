package com.cortex.backend.education.course.api;

import com.cortex.backend.education.course.api.dto.CourseRequest;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/education/course")
@RequiredArgsConstructor
@Tag(name = "Course", description = "Endpoints for managing courses")
public class CourseController {

  private final CourseService courseService;

  @GetMapping
  @Operation(summary = "Get all courses", description = "Retrieves a list of all courses")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  public ResponseEntity<List<CourseResponse>> getAllCourses() {
    return ResponseEntity.ok(courseService.getAllCourses());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a course by ID", description = "Retrieves a course by its ID")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  @ApiResponse(responseCode = "404", description = "Course not found")
  public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
    return courseService.getCourseById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/slug/{slug}")
  @Operation(summary = "Get a course by slug", description = "Retrieves a course by its slug")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  @ApiResponse(responseCode = "404", description = "Course not found")
  public ResponseEntity<CourseResponse> getCourseBySlug(@PathVariable String slug) {
    return courseService.getCourseBySlug(slug)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Create a new course", description = "Creates a new course with optional image upload")
  @ApiResponse(responseCode = "201", description = "Course created successfully",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  public ResponseEntity<CourseResponse> createCourse(
      @RequestPart("courseData") @Valid CourseRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
    CourseResponse createdCourse = courseService.createCourse(request, image);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Update a course", description = "Updates an existing course with optional image upload")
  @ApiResponse(responseCode = "200", description = "Course updated successfully",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  @ApiResponse(responseCode = "404", description = "Course not found")
  public ResponseEntity<CourseResponse> updateCourse(
      @PathVariable Long id,
      @RequestPart("courseData") @Valid CourseRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
    CourseResponse updatedCourse = courseService.updateCourse(id, request, image);
    return ResponseEntity.ok(updatedCourse);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Delete a course", description = "Deletes an existing course")
  @ApiResponse(responseCode = "204", description = "Course deleted successfully")
  @ApiResponse(responseCode = "404", description = "Course not found")
  public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
    courseService.deleteCourse(id);
    return ResponseEntity.noContent().build();
  }
}