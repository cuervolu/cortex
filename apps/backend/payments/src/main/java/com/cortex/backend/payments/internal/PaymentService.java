package com.cortex.backend.payments.internal;

import com.cortex.backend.core.domain.Refund;
import com.cortex.backend.core.domain.Subscription;
import com.cortex.backend.core.domain.SubscriptionPlan;
import com.cortex.backend.core.domain.SubscriptionStatus;
import com.cortex.backend.core.domain.Transaction;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.payments.api.RefundRepository;
import com.cortex.backend.payments.api.SubscriptionPlanRepository;
import com.cortex.backend.payments.api.SubscriptionRepository;
import com.cortex.backend.payments.api.TransactionRepository;
import com.cortex.backend.user.repository.UserRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentRefund;
import com.mercadopago.resources.preference.Preference;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentClient paymentClient;
  private final PreferenceClient preferenceClient;
  private final UserRepository userRepository;
  private final TransactionRepository transactionRepository;
  private final SubscriptionPlanRepository subscriptionPlanRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final RefundRepository refundRepository;

  @Value("${application.mercadopago.urls.success}")
  private String successUrl;

  @Value("${application.mercadopago.urls.pending}")
  private String pendingUrl;

  @Value("${application.mercadopago.urls.failure}")
  private String failureUrl;

  @Transactional
  public Preference createPaymentPreference(Long userId, Long planId) throws MPException, MPApiException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new EntityNotFoundException("Subscription plan not found"));

    PreferenceRequest preferenceRequest = PreferenceRequest.builder()
        .items(Collections.singletonList(
            PreferenceItemRequest.builder()
                .title(plan.getName())
                .quantity(1)
                .unitPrice(plan.getPrice())
                .build()
        ))
        .payer(PreferencePayerRequest.builder()
            .email(user.getEmail())
            .build())
        .backUrls(PreferenceBackUrlsRequest.builder()
            .success(successUrl)
            .pending(pendingUrl)
            .failure(failureUrl)
            .build())
        .externalReference(plan.getId().toString()) // Store plan ID for later use
        .build();

    return preferenceClient.create(preferenceRequest);
  }

  @Transactional
  public void processPayment(Long paymentId) throws MPException, MPApiException {
    Payment payment = paymentClient.get(paymentId);
    User user = userRepository.findByEmail(payment.getPayer().getEmail())
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Transaction transaction = Transaction.builder()
        .mercadopagoTransactionId(payment.getId()) 
        .user(user)
        .amount(payment.getTransactionAmount())
        .currency(payment.getCurrencyId())
        .status(payment.getStatus())
        .paymentMethod(payment.getPaymentMethodId())
        .transactionDate(payment.getDateApproved())
        .description(payment.getDescription())
        .createdAt(LocalDate.now())
        .build();

    transactionRepository.save(transaction);

    if ("approved".equals(payment.getStatus())) {
      activateOrRenewSubscription(user, payment);
    }
  }

  private void activateOrRenewSubscription(User user, Payment payment) {
    Long planId = Long.parseLong(payment.getExternalReference());
    SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new EntityNotFoundException("Subscription plan not found"));

    Subscription subscription = subscriptionRepository
        .findByUserAndStatusAndPlanId(user, SubscriptionStatus.ACTIVE, planId)
        .orElse(Subscription.builder()
            .user(user)
            .plan(plan)
            .status(SubscriptionStatus.ACTIVE)
            .startDate(LocalDate.now())
            .createdAt(LocalDate.now())
            .build());

    subscription.setNextBillingDate(LocalDate.now().plusDays(plan.getIntervalCount()));
    subscription.setUpdatedAt(LocalDate.now());

    subscriptionRepository.save(subscription);
  }

  public Payment getPaymentInfo(Long paymentId) throws MPException, MPApiException {
    return paymentClient.get(paymentId);
  }

  @Transactional
  public void refundPayment(Long paymentId, String reason) throws MPException, MPApiException {
    PaymentRefund mpRefund = paymentClient.refund(paymentId);
    Transaction transaction = transactionRepository.findByMercadopagoTransactionId(paymentId)
        .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

    // Update transaction status
    transaction.setStatus("refunded");
    transaction.setUpdatedAt(LocalDate.now());
    transactionRepository.save(transaction);

    // Create and save Refund entity
    Refund refund = Refund.builder()
        .transaction(transaction)
        .amount(mpRefund.getAmount())
        .refundDate(OffsetDateTime.now())
        .reason(reason)
        .status("completed")
        .createdAt(LocalDate.now())
        .build();
    refundRepository.save(refund);

    // Update all active subscriptions for this user
    List<Subscription> activeSubscriptions = subscriptionRepository
        .findByUserAndStatus(transaction.getUser(), SubscriptionStatus.ACTIVE);

    for (Subscription subscription : activeSubscriptions) {
      subscription.setStatus(SubscriptionStatus.CANCELLED);
      subscription.setUpdatedAt(LocalDate.now());
      subscriptionRepository.save(subscription);
    }

    if (activeSubscriptions.isEmpty()) {
      log.warn("No active subscriptions found for user {} during refund process", transaction.getUser().getId());
    }

    log.info("Refund processed successfully for payment ID: {}", paymentId);
  }

}