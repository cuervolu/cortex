package com.cortex.backend.payments.internal;

import static com.cortex.backend.core.domain.SubscriptionStatus.ACTIVE;
import static com.cortex.backend.core.domain.SubscriptionStatus.CANCELLED;
import static com.cortex.backend.core.domain.SubscriptionStatus.PENDING;

import com.cortex.backend.core.domain.Subscription;
import com.cortex.backend.core.domain.SubscriptionPlan;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.payments.api.SubscriptionPlanRepository;
import com.cortex.backend.payments.api.SubscriptionRepository;
import com.cortex.backend.user.repository.UserRepository;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

  private final PreferenceClient preferenceClient;

  private final UserRepository userRepository;

  private final SubscriptionPlanRepository subscriptionPlanRepository;

  private final SubscriptionRepository subscriptionRepository;

  @Value("${application.mercadopago.subscription.urls.success}")
  private String subscriptionSuccessUrl;

  @Value("${application.mercadopago.subscription.urls.pending}")
  private String subscriptionPendingUrl;

  @Value("${application.mercadopago.subscription.urls.failure}")
  private String subscriptionFailureUrl;

  public Preference createSubscription(Long userId, Long planId)
      throws MPException, MPApiException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new EntityNotFoundException("Subscription plan not found"));

    PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
        .title(plan.getName())
        .quantity(1)
        .unitPrice(plan.getPrice())
        .build();

    PreferenceRequest preferenceRequest = PreferenceRequest.builder()
        .items(Collections.singletonList(itemRequest))
        .payer(PreferencePayerRequest.builder()
            .email(user.getEmail())
            .build())
        .backUrls(PreferenceBackUrlsRequest.builder()
            .success(subscriptionSuccessUrl)
            .pending(subscriptionPendingUrl)
            .failure(subscriptionFailureUrl)
            .build())
        .autoReturn("approved")
        .build();

    Preference createdPreference = preferenceClient.create(preferenceRequest);

    // Create a pending subscription
    Subscription subscription = Subscription.builder()
        .user(user)
        .plan(plan)
        .status(PENDING)
        .startDate(LocalDate.now())
        .createdAt(LocalDate.now())
        .mercadopagoSubscriptionId(createdPreference.getId())
        .build();

    subscriptionRepository.save(subscription);

    return createdPreference;
  }

  public void activateSubscription(String preferenceId) {
    Subscription subscription = subscriptionRepository.findByMercadopagoSubscriptionId(preferenceId)
        .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));

    subscription.setStatus(ACTIVE);
    subscription.setNextBillingDate(
        LocalDate.now().plusDays(subscription.getPlan().getIntervalCount()));
    subscription.setUpdatedAt(LocalDate.now());

    subscriptionRepository.save(subscription);
  }

  public void cancelSubscription(Long subscriptionId) {
    Subscription subscription = subscriptionRepository.findById(subscriptionId)
        .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));

    subscription.setStatus(CANCELLED);
    subscription.setUpdatedAt(LocalDate.now());

    subscriptionRepository.save(subscription);
  }

}
