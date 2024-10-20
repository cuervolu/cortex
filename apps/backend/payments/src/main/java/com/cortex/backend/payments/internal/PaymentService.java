package com.cortex.backend.payments.internal;

import static java.time.OffsetDateTime.*;

import com.cortex.backend.core.domain.*;
import com.cortex.backend.lemonsqueezy.checkouts.*;
import com.cortex.backend.lemonsqueezy.subscriptions.*;
import com.cortex.backend.lemonsqueezy.licensekeys.*;
import com.cortex.backend.lemonsqueezy.webhook.Webhook;
import com.cortex.backend.lemonsqueezy.webhook.WebhookCreateRequest;
import com.cortex.backend.lemonsqueezy.webhook.WebhookFilter;
import com.cortex.backend.lemonsqueezy.webhook.WebhookService;
import com.cortex.backend.lemonsqueezy.webhook.WebhookUpdateRequest;
import com.cortex.backend.payments.api.*;
import com.cortex.backend.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

  private final CheckoutService checkoutService;
  private final SubscriptionsService subscriptionsService;
  private final LicenseKeyService licenseKeyService;
  private final UserRepository userRepository;
  private final TransactionRepository transactionRepository;
  private final SubscriptionPlanRepository subscriptionPlanRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final RefundRepository refundRepository;
  private final WebhookService webhookService;

  @Transactional
  public Transaction createCheckout(Long userId, Long planId) throws Exception {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new RuntimeException("Subscription plan not found"));

    CheckoutRequest checkoutRequest = buildCheckoutRequest(user, plan);
    Checkout checkout = checkoutService.createCheckout(checkoutRequest);

    Transaction transaction = Transaction.builder()
        .user(user)
        .amount(plan.getPrice())
        .currency(plan.getCurrencyId())
        .status("pending")
        .paymentMethod("lemon_squeezy")
        .transactionDate(now())
        .description("Checkout for " + plan.getName())
        .createdAt(LocalDate.now())
        .lemonSqueezyTransactionId(checkout.getId())
        .build();

    return transactionRepository.save(transaction);
  }

  private CheckoutRequest buildCheckoutRequest(User user, SubscriptionPlan plan) {
    Map<String, Object> checkoutData = new HashMap<>();
    checkoutData.put("email", user.getEmail());
    checkoutData.put("name", user.getFullName());

    return CheckoutRequest.builder()
        .storeId(1L) // Replace with your actual store ID
        .variantId(Long.parseLong(plan.getLemonSqueezyPlanId()))
        .customPrice(plan.getPrice().multiply(new BigDecimal(100)).intValue())
        .checkoutData(checkoutData)
        .build();
  }

  @Transactional
  public com.cortex.backend.core.domain.Subscription createSubscription(Long userId, Long planId)
      throws Exception {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
        .orElseThrow(() -> new RuntimeException("Subscription plan not found"));

    // Create a checkout first
    Checkout checkout = checkoutService.createCheckout(buildCheckoutRequest(user, plan));

    // In a real-world scenario, you'd wait for a webhook to confirm the payment before creating the subscription
    // For this example, we'll assume the payment is successful immediately

    SubscriptionFilter filter = SubscriptionFilter.builder()
        .userEmail(user.getEmail())
        .build();

    // Fetch the subscription from Lemon Squeezy
    com.cortex.backend.lemonsqueezy.subscriptions.Subscription lemonSqueezySubscription =
        subscriptionsService.listSubscriptions(filter, null).getData().get(0);

    com.cortex.backend.core.domain.Subscription subscription = com.cortex.backend.core.domain.Subscription.builder()
        .user(user)
        .plan(plan)
        .status(mapSubscriptionStatus(lemonSqueezySubscription.getStatus()))
        .startDate(LocalDate.now())
        .nextBillingDate(lemonSqueezySubscription.getRenewsAt().toLocalDate())
        .lemonSqueezySubscriptionId(lemonSqueezySubscription.getId())
        .createdAt(LocalDate.now())
        .build();

    return subscriptionRepository.save(subscription);
  }

  private SubscriptionStatus mapSubscriptionStatus(String lemonSqueezyStatus) {
    return switch (lemonSqueezyStatus) {
      case "on_trial" -> SubscriptionStatus.TRIAL;
      case "active" -> SubscriptionStatus.ACTIVE;
      case "paused" -> SubscriptionStatus.PAUSED;
      case "past_due" -> SubscriptionStatus.PAST_DUE;
      case "unpaid" -> SubscriptionStatus.UNPAID;
      case "cancelled", "expired" -> SubscriptionStatus.CANCELLED;
      default ->
          throw new IllegalArgumentException("Unknown subscription status: " + lemonSqueezyStatus);
    };
  }

  @Transactional
  public Refund createRefund(Long transactionId, BigDecimal amount, String reason)
      throws Exception {
    Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new RuntimeException("Transaction not found"));

    // In a real-world scenario, you'd call Lemon Squeezy's API to process the refund
    // For this example, we'll assume the refund is successful immediately

    Refund refund = Refund.builder()
        .transaction(transaction)
        .amount(amount)
        .refundDate(now())
        .reason(reason)
        .status("completed")
        .createdAt(LocalDate.now())
        .build();

    return refundRepository.save(refund);
  }

  @Transactional
  public com.cortex.backend.core.domain.Subscription cancelSubscription(Long subscriptionId)
      throws Exception {
    com.cortex.backend.core.domain.Subscription subscription = subscriptionRepository.findById(
            subscriptionId)
        .orElseThrow(() -> new RuntimeException("Subscription not found"));

    var cancelledSubscription =
        subscriptionsService.cancelSubscription(subscription.getLemonSqueezySubscriptionId());

    subscription.setStatus(SubscriptionStatus.CANCELLED);
    subscription.setUpdatedAt(LocalDate.now());

    return subscriptionRepository.save(subscription);
  }

  @Transactional
  public LicenseKey getLicenseKey(String licenseKeyId) throws Exception {
    return licenseKeyService.getLicenseKey(licenseKeyId);
  }

  @Transactional
  public Webhook createWebhook(Long storeId, String url, List<String> events, String secret)
      throws Exception {
    WebhookCreateRequest createRequest = WebhookCreateRequest.builder()
        .storeId(storeId)
        .url(url)
        .events(events)
        .secret(secret)
        .build();

    return webhookService.createWebhook(createRequest);
  }

  @Transactional
  public Webhook updateWebhook(String webhookId, String url, List<String> events, String secret)
      throws Exception {
    WebhookUpdateRequest updateRequest = WebhookUpdateRequest.builder()
        .url(url)
        .events(events)
        .secret(secret)
        .build();

    return webhookService.updateWebhook(webhookId, updateRequest);
  }

  @Transactional
  public void deleteWebhook(String webhookId) throws Exception {
    webhookService.deleteWebhook(webhookId);
  }

  @Transactional(readOnly = true)
  public Webhook getWebhook(String webhookId) throws Exception {
    return webhookService.getWebhook(webhookId);
  }

  @Transactional(readOnly = true)
  public List<Webhook> listWebhooks(Long storeId) throws Exception {
    WebhookFilter filter = WebhookFilter.builder()
        .storeId(storeId)
        .build();

    return webhookService.listWebhooks(filter, null).getData();
  }

  @Transactional
  public void handleWebhookEvent(String eventType, Map<String, Object> eventData) {
    // Implement logic to handle different webhook events
    switch (eventType) {
      case "order_created":
        handleOrderCreated(eventData);
        break;
      case "subscription_created":
        handleSubscriptionCreated(eventData);
        break;
      case "subscription_updated":
        handleSubscriptionUpdated(eventData);
        break;
      case "subscription_cancelled":
        handleSubscriptionCancelled(eventData);
        break;
      // Add more cases for other event types as needed
      default:
        log.warn("Unhandled webhook event type: {}", eventType);
    }
  }

  private void handleOrderCreated(Map<String, Object> eventData) {
    // Implement logic to handle order creation
    // Update local database, send notifications, etc.
  }

  private void handleSubscriptionCreated(Map<String, Object> eventData) {
    // Implement logic to handle subscription creation
    // Update local database, provision resources, etc.
  }

  private void handleSubscriptionUpdated(Map<String, Object> eventData) {
    // Implement logic to handle subscription updates
    // Update local database, adjust resources, etc.
  }

  private void handleSubscriptionCancelled(Map<String, Object> eventData) {
    // Implement logic to handle subscription cancellation
    // Update local database, revoke access, etc.
  }
}