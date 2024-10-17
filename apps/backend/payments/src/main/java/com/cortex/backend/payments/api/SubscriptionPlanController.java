package com.cortex.backend.payments.api;

import com.cortex.backend.payments.api.dto.CreateSubscriptionPlanDTO;
import com.cortex.backend.payments.api.dto.SubscriptionPlanDTO;
import com.cortex.backend.payments.internal.SubscriptionPlanMapper;
import com.cortex.backend.payments.internal.SubscriptionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription-plans")
@RequiredArgsConstructor
@Tag(name = "Subscription Plans", description = "Endpoints for managing subscription plans")
public class SubscriptionPlanController {

  private final SubscriptionPlanService subscriptionPlanService;
  private final SubscriptionPlanMapper subscriptionPlanMapper;

  @PostMapping
  @Operation(summary = "Create a new subscription plan")
  @ApiResponse(responseCode = "200", description = "Subscription plan created successfully")
  public ResponseEntity<SubscriptionPlanDTO> createPlan(@Valid @RequestBody CreateSubscriptionPlanDTO createPlanDTO) {
    var plan = subscriptionPlanMapper.toEntity(createPlanDTO);
    var createdPlan = subscriptionPlanService.createPlan(plan);
    return ResponseEntity.ok(subscriptionPlanMapper.toDTO(createdPlan));
  }


  @PutMapping("/{id}")
  @Operation(summary = "Update an existing subscription plan")
  @ApiResponse(responseCode = "200", description = "Subscription plan updated successfully")
  public ResponseEntity<SubscriptionPlanDTO> updatePlan(@PathVariable Long id, @RequestBody SubscriptionPlanDTO planDTO) {
    var plan = subscriptionPlanMapper.toEntity(planDTO);
    var updatedPlan = subscriptionPlanService.updatePlan(id, plan);
    return ResponseEntity.ok(subscriptionPlanMapper.toDTO(updatedPlan));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a subscription plan")
  @ApiResponse(responseCode = "204", description = "Subscription plan deleted successfully")
  public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
    subscriptionPlanService.deletePlan(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get a specific subscription plan")
  @ApiResponse(responseCode = "200", description = "Subscription plan retrieved successfully")
  public ResponseEntity<SubscriptionPlanDTO> getPlan(@PathVariable Long id) {
    var plan = subscriptionPlanService.getPlan(id);
    return ResponseEntity.ok(subscriptionPlanMapper.toDTO(plan));
  }

  @GetMapping
  @Operation(summary = "Get all subscription plans")
  @ApiResponse(responseCode = "200", description = "List of subscription plans retrieved successfully")
  public ResponseEntity<List<SubscriptionPlanDTO>> getAllPlans() {
    var plans = subscriptionPlanService.getAllPlans();
    return ResponseEntity.ok(subscriptionPlanMapper.toDTOList(plans));
  }

  @PostMapping("/{id}/sync")
  @Operation(summary = "Synchronize a subscription plan with MercadoPago")
  @ApiResponse(responseCode = "200", description = "Subscription plan synchronized successfully")
  public ResponseEntity<SubscriptionPlanDTO> syncPlanWithMercadoPago(@PathVariable Long id) {
    var syncedPlan = subscriptionPlanService.syncPlanWithMercadoPago(id);
    return ResponseEntity.ok(subscriptionPlanMapper.toDTO(syncedPlan));
  }
}