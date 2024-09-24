package com.cortex.backend.education.course.api;

import com.cortex.backend.education.course.api.dto.CourseRequest;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.course.api.dto.CourseUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  @PostMapping()
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Create a new course", description = "Creates a new course")
  @ApiResponse(responseCode = "201", description = "Course created successfully",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  public ResponseEntity<CourseResponse> createCourse(
      @Valid @RequestBody CourseRequest request
  ) {
    CourseResponse createdCourse = courseService.createCourse(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
  }

  @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Upload a course image", description = "Uploads an image for a course")
  @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  public ResponseEntity<CourseResponse> uploadRoadmapImage(
      @PathVariable Long id,
      @RequestParam("image") MultipartFile image,
      @RequestParam(value = "altText", required = false) String altText) throws IOException {
    CourseResponse updatedCourse = courseService.uploadCourseImage(id, image, altText);
    return ResponseEntity.ok(updatedCourse);
  }


  @PatchMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Update a course", description = "Updates an existing course")
  @ApiResponse(responseCode = "200", description = "Course updated successfully",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  @ApiResponse(responseCode = "404", description = "Course not found")
  public ResponseEntity<CourseResponse> updateCourse(
      @PathVariable Long id,
      @RequestBody @Valid CourseUpdateRequest request){
    CourseResponse updatedCourse = courseService.updateCourse(id, request);
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