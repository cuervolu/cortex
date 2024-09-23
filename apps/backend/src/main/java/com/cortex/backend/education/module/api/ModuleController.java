package com.cortex.backend.education.module.api;

import com.cortex.backend.education.module.api.dto.ModuleRequest;
import com.cortex.backend.education.module.api.dto.ModuleResponse;
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
@RequestMapping("/education/module")
@RequiredArgsConstructor
@Tag(name = "Module", description = "Endpoints for managing modules")
public class ModuleController {

  private final ModuleService moduleService;

  @GetMapping
  @Operation(summary = "Get all modules", description = "Retrieves a list of all modules")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  public ResponseEntity<List<ModuleResponse>> getAllModules() {
    return ResponseEntity.ok(moduleService.getAllModules());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a module by ID", description = "Retrieves a module by its ID")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  @ApiResponse(responseCode = "404", description = "Module not found")
  public ResponseEntity<ModuleResponse> getModuleById(@PathVariable Long id) {
    return moduleService.getModuleById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/slug/{slug}")
  @Operation(summary = "Get a module by slug", description = "Retrieves a module by its slug")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  @ApiResponse(responseCode = "404", description = "Module not found")
  public ResponseEntity<ModuleResponse> getModuleBySlug(@PathVariable String slug) {
    return moduleService.getModuleBySlug(slug)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Create a new module", description = "Creates a new module with optional image upload")
  @ApiResponse(responseCode = "201", description = "Module created successfully",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  public ResponseEntity<ModuleResponse> createModule(
      @RequestPart("moduleData") @Valid ModuleRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
    ModuleResponse createdModule = moduleService.createModule(request, image);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdModule);
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Update a module", description = "Updates an existing module with optional image upload")
  @ApiResponse(responseCode = "200", description = "Module updated successfully",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  @ApiResponse(responseCode = "404", description = "Module not found")
  public ResponseEntity<ModuleResponse> updateModule(
      @PathVariable Long id,
      @RequestPart("moduleData") @Valid ModuleRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
    ModuleResponse updatedModule = moduleService.updateModule(id, request, image);
    return ResponseEntity.ok(updatedModule);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Delete a module", description = "Deletes an existing module")
  @ApiResponse(responseCode = "204", description = "Module deleted successfully")
  @ApiResponse(responseCode = "404", description = "Module not found")
  public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
    moduleService.deleteModule(id);
    return ResponseEntity.noContent().build();
  }
}