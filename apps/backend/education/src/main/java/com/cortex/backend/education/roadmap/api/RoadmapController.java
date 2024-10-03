package com.cortex.backend.education.roadmap.api;

import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/education/roadmap")
@RequiredArgsConstructor
@Tag(name = "Roadmap", description = "Endpoints for managing roadmaps")
public class RoadmapController {

  private final RoadmapService roadmapService;

  @GetMapping
  @Operation(summary = "Get all roadmaps", description = "Retrieves a list of all roadmaps")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  public ResponseEntity<List<RoadmapResponse>> getAllRoadmaps() {
    return ResponseEntity.ok(roadmapService.getAllRoadmaps());
  }

  @GetMapping("/{slug}")
  @Operation(summary = "Get a roadmap by slug", description = "Retrieves a roadmap by its slug")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
  @ApiResponse(responseCode = "404", description = "Roadmap not found")
  public ResponseEntity<RoadmapDetails> getRoadmapBySlug(@PathVariable String slug) {
    return roadmapService.getRoadmapBySlug(slug)
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
}