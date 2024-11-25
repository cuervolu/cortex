package com.cortex.backend.payments.api;

import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseActivationResponse;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseDeactivationResponse;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseValidationResponse;
import com.cortex.backend.payments.internal.PaymentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subscriptions")
@Tag(name = "Subscriptions", description = "API endpoints for managing license subscriptions")
public class SubscriptionsController {

  private final PaymentsService paymentsService;

  @Operation(summary = "Validate a license key",
      description = "Validates a license key with its corresponding instance ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "License validated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid license key or instance ID"),
      @ApiResponse(responseCode = "500", description = "Failed to validate license")
  })
  @GetMapping("/validate")
  public ResponseEntity<LicenseValidationResponse> validateLicense(
      @Parameter(description = "License key to validate", required = true)
      @RequestParam("license_key") String licenseKey,
      @Parameter(description = "Instance ID associated with the license", required = true)
      @RequestParam("instance_id") String instanceId) {

    LicenseValidationResponse response = paymentsService.validateLicense(licenseKey, instanceId);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Activate a license key",
      description = "Activates a license key for a specific instance")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "License activated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid license key or instance name"),
      @ApiResponse(responseCode = "500", description = "Failed to activate license")
  })
  @PostMapping("/activate")
  public ResponseEntity<LicenseActivationResponse> activateLicense(
      @Parameter(description = "License key to activate", required = true)
      @RequestParam("license_key") String licenseKey,
      @Parameter(description = "Name of the instance to activate", required = true)
      @RequestParam("instance_name") String instanceName) {

    LicenseActivationResponse response = paymentsService.activateLicense(licenseKey, instanceName);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Deactivate a license key",
      description = "Deactivates a license key for a specific instance")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "License deactivated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid license key or instance ID"),
      @ApiResponse(responseCode = "500", description = "Failed to deactivate license")
  })
  @DeleteMapping("/deactivate")
  public ResponseEntity<LicenseDeactivationResponse> deactivateLicense(
      @Parameter(description = "License key to deactivate", required = true)
      @RequestParam("license_key") String licenseKey,
      @Parameter(description = "Instance ID to deactivate", required = true)
      @RequestParam("instance_id") String instanceId) {

    LicenseDeactivationResponse response = paymentsService.deactivateLicense(licenseKey, instanceId);
    return ResponseEntity.ok(response);
  }
}