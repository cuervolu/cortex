package com.cortex.backend.payments.internal;

import com.cortex.backend.core.domain.IntervalUnit;
import com.cortex.backend.core.domain.SubscriptionPlan;
import com.cortex.backend.payments.api.SubscriptionPlanRepository;
import com.cortex.backend.payments.model.MPSubscriptionPlan;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

  private final SubscriptionPlanRepository subscriptionPlanRepository;
  private final MercadoPagoClient mercadoPagoClient;

  private static final String PLAN_NOT_FOUND = "Subscription plan not found";

  @Transactional
  public SubscriptionPlan createPlan(SubscriptionPlan plan) {
    plan.setCreatedAt(LocalDate.now());
    MPSubscriptionPlan mpPlan = mercadoPagoClient.createPlan(plan);
    plan.setMercadoPagoPlanId(mpPlan.getId());
    return subscriptionPlanRepository.save(plan);
  }

  @Transactional
  public SubscriptionPlan updatePlan(Long planId, SubscriptionPlan updatedPlan) {
    SubscriptionPlan existingPlan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new EntityNotFoundException(PLAN_NOT_FOUND));

    existingPlan.setName(updatedPlan.getName());
    existingPlan.setDescription(updatedPlan.getDescription());
    existingPlan.setPrice(updatedPlan.getPrice());
    existingPlan.setIntervalUnit(updatedPlan.getIntervalUnit());
    existingPlan.setIntervalCount(updatedPlan.getIntervalCount());
    existingPlan.setCurrencyId(updatedPlan.getCurrencyId());
    existingPlan.setUpdatedAt(LocalDate.now());

    MPSubscriptionPlan mpPlan = mercadoPagoClient.updatePlan(existingPlan.getMercadoPagoPlanId(),
        existingPlan);
    existingPlan.setMercadoPagoPlanId(mpPlan.getId());

    return subscriptionPlanRepository.save(existingPlan);
  }

  @Transactional
  public void deletePlan(Long planId) {
    SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new EntityNotFoundException(PLAN_NOT_FOUND));

    mercadoPagoClient.deletePlan(plan.getMercadoPagoPlanId());
    subscriptionPlanRepository.delete(plan);
  }

  public SubscriptionPlan getPlan(Long planId) {
    return subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new EntityNotFoundException(PLAN_NOT_FOUND));
  }

  public List<SubscriptionPlan> getAllPlans() {
    return (List<SubscriptionPlan>) subscriptionPlanRepository.findAll();
  }

  public SubscriptionPlan syncPlanWithMercadoPago(Long planId) {
    SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new EntityNotFoundException(PLAN_NOT_FOUND));

    MPSubscriptionPlan mpPlan = mercadoPagoClient.getPlan(plan.getMercadoPagoPlanId());

    // Update plan with MercadoPago data
    plan.setName(mpPlan.getReason());
    plan.setPrice(mpPlan.getAutoRecurring().getTransactionAmount());
    plan.setIntervalCount(mpPlan.getAutoRecurring().getFrequency());
    plan.setIntervalUnit(
        IntervalUnit.valueOf(mpPlan.getAutoRecurring().getFrequencyType().toUpperCase()));
    plan.setCurrencyId(mpPlan.getAutoRecurring().getCurrencyId());
    plan.setUpdatedAt(LocalDate.now());

    return subscriptionPlanRepository.save(plan);
  }
}