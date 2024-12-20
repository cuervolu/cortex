package com.cortex.backend.education.roadmap.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.roadmap.api.dto.CourseAssignmentRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapEnrollmentResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapUpdateRequest;
import com.cortex.backend.education.roadmap.internal.RoadmapEnrollmentService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/education/roadmap")
@RequiredArgsConstructor
@Tag(name = "Roadmap", description = "Endpoints for managing roadmaps")
public class RoadmapController {

  private final RoadmapService roadmapService;
  private final RoadmapEnrollmentService enrollmentService;

  @GetMapping
  @Operation(summary = "Get all published roadmaps", description = "Retrieves a list of all published roadmaps")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  public ResponseEntity<PageResponse<RoadmapResponse>> getAllPublishedRoadmaps(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sort", required = false) String[] sort
  ) {
    return ResponseEntity.ok(roadmapService.getAllPublishedRoadmaps(page, size, sort));
  }

  @GetMapping("/admin")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Get all roadmaps (including unpublished)",
      description = "Retrieves a list of all roadmaps. Requires ADMIN or MODERATOR role")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  public ResponseEntity<PageResponse<RoadmapResponse>> getAllRoadmaps(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sort", required = false) String[] sort
  ) {
    return ResponseEntity.ok(roadmapService.getAllRoadmaps(page, size, sort));
  }

  @GetMapping("/{slug}")
  @Operation(summary = "Get a roadmap by slug", description = "Retrieves a roadmap by its slug")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<RoadmapDetails> getRoadmapBySlug(
      @PathVariable String slug,
      Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return roadmapService.getRoadmapBySlug(slug, user)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{roadmapSlug}/course/{courseSlug}")
  @Operation(summary = "Get a course from a roadmap",
      description = "Retrieves a specific course from a roadmap using their slugs")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = CourseResponse.class)))
  @ApiResponse(responseCode = "404", description = "Course not found in the roadmap")
  public ResponseEntity<CourseResponse> getCourseFromRoadmap(
      @PathVariable String roadmapSlug,
      @PathVariable String courseSlug) {
    return roadmapService.getCourseFromRoadmap(roadmapSlug, courseSlug)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping()
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Create a new roadmap", description = "Creates a new roadmap")
  @ApiResponse(responseCode = "201", description = "Roadmap created successfully",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  public ResponseEntity<RoadmapResponse> createRoadmap(
      @Valid @RequestBody RoadmapRequest request) {
    RoadmapResponse createdRoadmap = roadmapService.createRoadmap(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdRoadmap);
  }

  @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Upload a roadmap image", description = "Uploads an image for a roadmap")
  @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  public ResponseEntity<RoadmapResponse> uploadRoadmapImage(
      @PathVariable Long id,
      @RequestParam("image") MultipartFile image,
      @RequestParam(value = "altText", required = false) String altText) throws IOException {
    RoadmapResponse updatedRoadmap = roadmapService.uploadRoadmapImage(id, image, altText);
    return ResponseEntity.ok(updatedRoadmap);
  }


  @PatchMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Update a roadmap", description = "Updates an existing roadmap")
  @ApiResponse(responseCode = "200", description = "Roadmap updated successfully",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<RoadmapResponse> updateRoadmap(
      @PathVariable Long id,
      @RequestBody @Valid RoadmapUpdateRequest request) {

    RoadmapResponse updatedRoadmap = roadmapService.updateRoadmap(id, request);
    return ResponseEntity.ok(updatedRoadmap);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Delete a roadmap", description = "Deletes an existing roadmap")
  @ApiResponse(responseCode = "204", description = "Roadmap deleted successfully")
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<Void> deleteRoadmap(@PathVariable Long id) {
    roadmapService.deleteRoadmap(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/available-courses")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(
      summary = "Get available courses for roadmap",
      description = "Retrieves courses not assigned to the roadmap. For admin/moderator users, can include unpublished courses."
  )
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = PageResponse.class)))
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<PageResponse<CourseResponse>> getAvailableCourses(
      @PathVariable Long id,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sort", required = false) String[] sort,
      @RequestParam(name = "includeUnpublished", defaultValue = "false") boolean includeUnpublished) {

    return roadmapService.getAvailableCourses(id, page, size, sort, includeUnpublished)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{id}/courses")
  @Operation(summary = "Get roadmap courses", description = "Retrieves all courses assigned to a roadmap")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = PageResponse.class)))
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<PageResponse<CourseResponse>> getRoadmapCourses(
      @PathVariable Long id,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sort", required = false) String[] sort) {

    return roadmapService.getRoadmapCourses(id, page, size, sort)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}/courses")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(
      summary = "Assign courses to roadmap",
      description = "Updates the courses assigned to a roadmap with their display order"
  )
  @ApiResponse(responseCode = "200", description = "Courses assigned successfully")
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<Void> assignCoursesToRoadmap(
      @PathVariable Long id,
      @Valid @RequestBody CourseAssignmentRequest request) {
    roadmapService.assignCoursesToRoadmap(id, request.assignments());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/enroll")
  @Operation(
      summary = "Enroll in roadmap",
      description = "Enrolls the authenticated user in a roadmap"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Successfully enrolled",
      content = @Content(schema = @Schema(implementation = RoadmapEnrollmentResponse.class))
  )
  @ApiResponse(responseCode = "400", description = "User already enrolled")
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<RoadmapEnrollmentResponse> enrollInRoadmap(
      @PathVariable Long id,
      Authentication authentication
  ) {
    User user = (User) authentication.getPrincipal();
    RoadmapEnrollmentResponse enrollment = enrollmentService.enrollUserInRoadmap(user.getId(), id);
    return ResponseEntity.ok(enrollment);
  }

  @GetMapping("/enrollments")
  @Operation(
      summary = "Get user enrollments",
      description = "Gets all roadmap enrollments for the authenticated user"
  )
  @ApiResponse(
      responseCode = "200",
      description = "List of enrollments retrieved successfully",
      content = @Content(schema = @Schema(implementation = RoadmapEnrollmentResponse.class))
  )
  public ResponseEntity<List<RoadmapEnrollmentResponse>> getUserEnrollments(
      Authentication authentication
  ) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(enrollmentService.getUserEnrollments(user.getId()));
  }



}