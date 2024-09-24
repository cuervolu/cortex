package com.cortex.backend.education.module.api;

import com.cortex.backend.education.module.api.dto.ModuleRequest;
import com.cortex.backend.education.module.api.dto.ModuleResponse;
import com.cortex.backend.education.module.api.dto.ModuleUpdateRequest;
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


  @PostMapping
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Create a new module", description = "Creates a new module")
  @ApiResponse(responseCode = "201", description = "Module created successfully",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  public ResponseEntity<ModuleResponse> createModule(@Valid @RequestBody ModuleRequest request) {
    ModuleResponse createdModule = moduleService.createModule(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdModule);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Update a module", description = "Updates an existing module")
  @ApiResponse(responseCode = "200", description = "Module updated successfully",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  @ApiResponse(responseCode = "404", description = "Module not found")
  public ResponseEntity<ModuleResponse> updateModule(
      @PathVariable Long id,
      @Valid @RequestBody ModuleUpdateRequest request) {
    ModuleResponse updatedModule = moduleService.updateModule(id, request);
    return ResponseEntity.ok(updatedModule);
  }

  @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Upload a module image", description = "Uploads an image for a module")
  @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
      content = @Content(schema = @Schema(implementation = ModuleResponse.class)))
  public ResponseEntity<ModuleResponse> uploadModuleImage(
      @PathVariable Long id,
      @RequestParam("image") MultipartFile image,
      @RequestParam(value = "altText", required = false) String altText) throws IOException {
    ModuleResponse updatedModule = moduleService.uploadModuleImage(id, image, altText);
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