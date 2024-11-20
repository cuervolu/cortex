package com.cortex.backend.payments.api;

import com.cortex.backend.core.domain.Plan;
import com.cortex.backend.payments.api.dto.PlanSyncResponse;
import com.cortex.backend.payments.internal.PlanSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
@Tag(name = "Plans", description = "Plan management endpoints")
public class PlanSyncController {

  private final PlanSyncService planSyncService;

  @PostMapping("/sync")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Synchronize plans with Lemon Squeezy",
      description = "Fetches all subscription plans from Lemon Squeezy and synchronizes them with the local database. Requires ADMIN role.",
      security = { @SecurityRequirement(name = "bearerAuth") }
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Plans synchronized successfully"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Unauthorized - Authentication is required"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden - User doesn't have admin privileges"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error during synchronization"
      )
  })
  public ResponseEntity<PlanSyncResponse> syncPlans() {
    try {
      log.info("Starting plan synchronization process");
      long startTime = System.currentTimeMillis();

      List<Plan> syncedPlans = planSyncService.syncPlans();

      long endTime = System.currentTimeMillis();
      String duration = String.format("%.2f", (endTime - startTime) / 1000.0);

      PlanSyncResponse response = PlanSyncResponse.builder()
          .success(true)
          .message(String.format("Successfully synchronized %d plans", syncedPlans.size()))
          .syncedPlansCount(syncedPlans.size())
          .executionTimeSeconds(duration)
          .plans(syncedPlans)
          .build();

      log.info("Plan synchronization completed in {} seconds. Synced {} plans",
          duration, syncedPlans.size());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error during plan synchronization", e);

      PlanSyncResponse response = PlanSyncResponse.builder()
          .success(false)
          .message("Error synchronizing plans: " + e.getMessage())
          .build();

      return ResponseEntity.internalServerError().body(response);
    }
  }
}