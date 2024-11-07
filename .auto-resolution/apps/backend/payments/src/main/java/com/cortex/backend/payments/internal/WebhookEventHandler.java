package com.cortex.backend.payments.internal;

import com.cortex.backend.core.common.exception.PaymentServiceException;
import com.cortex.backend.core.domain.Subscription;
import com.cortex.backend.lemonsqueezy.webhook.WebhookEventType;
import com.cortex.backend.payments.api.SubscriptionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookEventHandler {

  private final SubscriptionRepository subscriptionRepository;

  private static final String DATA_FIELD = "data";
  private static final String ATTRIBUTES_FIELD = "attributes";
  private static final String STATUS_FIELD = "status";
  private static final String RENEWS_AT_FIELD = "renews_at";
  private static final String ENDS_AT_FIELD = "ends_at";

  @Transactional
  public void handleWebhookEvent(WebhookEventType eventType, JsonNode payload) {
    log.info("Processing webhook event: {}", eventType.getEventName());

    try {
      switch (eventType.getCategory()) {
        case ORDER -> handleOrderEvent(eventType, payload);
        case SUBSCRIPTION -> handleSubscriptionEvent(eventType, payload);
        case SUBSCRIPTION_PAYMENT -> handleSubscriptionPaymentEvent(eventType, payload);
        case LICENSE_KEY -> handleLicenseKeyEvent(eventType, payload);
      }
    } catch (Exception e) {
      log.error("Error processing webhook event: {}", eventType.getEventName(), e);
      throw new PaymentServiceException("Failed to process webhook event", e);
    }
  }

  private void handleOrderEvent(WebhookEventType eventType, JsonNode payload) {
    JsonNode data = getDataNode(payload);
    JsonNode attributes = getAttributesNode(data);

    switch (eventType) {
      case ORDER_CREATED -> log.info("Order created event received");
      case ORDER_REFUNDED -> log.info("Order refunded event received");
      default -> log.warn("Unhandled order event type: {}", eventType.getEventName());
    }
  }

  private void handleSubscriptionEvent(WebhookEventType eventType, JsonNode payload) {
    JsonNode data = getDataNode(payload);
    String subscriptionId = data.path("id").asText();
    JsonNode attributes = getAttributesNode(data);

    switch (eventType) {
      case SUBSCRIPTION_CREATED -> processSubscriptionCreated(subscriptionId, attributes);
      case SUBSCRIPTION_UPDATED -> processSubscriptionUpdated(subscriptionId, attributes);
      case SUBSCRIPTION_CANCELLED -> processSubscriptionCancelled(subscriptionId, attributes);
      case SUBSCRIPTION_RESUMED -> processSubscriptionResumed(subscriptionId, attributes);
      default -> log.warn("Unhandled subscription event type: {}", eventType.getEventName());
    }
  }

  private void handleSubscriptionPaymentEvent(WebhookEventType eventType, JsonNode payload) {
    JsonNode data = getDataNode(payload);
    String subscriptionId = data.path("id").asText();
    JsonNode attributes = getAttributesNode(data);

    switch (eventType) {
      case SUBSCRIPTION_PAYMENT_SUCCESS -> processSubscriptionPaymentSuccess(subscriptionId, attributes);
      case SUBSCRIPTION_PAYMENT_FAILED -> processSubscriptionPaymentFailed(subscriptionId, attributes);
      case SUBSCRIPTION_PAYMENT_RECOVERED -> processSubscriptionPaymentRecovered(subscriptionId, attributes);
      default -> log.warn("Unhandled subscription payment event type: {}", eventType.getEventName());
    }
  }

  private void handleLicenseKeyEvent(WebhookEventType eventType, JsonNode payload) {
    log.info("License key event received: {}", eventType.getEventName());
  }

  private void updateSubscriptionStatus(String subscriptionId, String status, JsonNode attributes) {
    subscriptionRepository.findByLemonSqueezyId(subscriptionId)
        .ifPresent(subscription -> {
          subscription.setStatus(status);
          updateSubscriptionFromAttributes(subscription, attributes);
          subscriptionRepository.save(subscription);
          log.info("Updated subscription status to {} for: {}", status, subscriptionId);
        });
  }

  private void updateSubscriptionFromAttributes(Subscription subscription, JsonNode attributes) {
    updateDateIfPresent(attributes, RENEWS_AT_FIELD, date -> subscription.setRenewsAt(date.toString()));
    updateDateIfPresent(attributes, ENDS_AT_FIELD, date -> subscription.setEndsAt(date.toString()));
  }

  private void updateDateIfPresent(JsonNode attributes, String field, Consumer<ZonedDateTime> setter) {
    if (attributes.has(field) && !attributes.path(field).isNull()) {
      try {
        ZonedDateTime dateTime = ZonedDateTime.parse(attributes.path(field).asText());
        setter.accept(dateTime);
      } catch (Exception e) {
        log.warn("Failed to parse date field {}: {}", field, e.getMessage());
      }
    }
  }

  private JsonNode getDataNode(JsonNode payload) {
    return Optional.ofNullable(payload.path(DATA_FIELD))
        .filter(JsonNode::isObject)
        .orElseThrow(() -> new IllegalArgumentException("Invalid payload: missing data field"));
  }

  private JsonNode getAttributesNode(JsonNode data) {
    return Optional.ofNullable(data.path(ATTRIBUTES_FIELD))
        .filter(JsonNode::isObject)
        .orElseThrow(() -> new IllegalArgumentException("Invalid data: missing attributes field"));
  }

  private void processSubscriptionCreated(String subscriptionId, JsonNode attributes) {
    String status = attributes.path(STATUS_FIELD).asText();
    updateSubscriptionStatus(subscriptionId, status, attributes);
    log.info("Created subscription with id: {}", subscriptionId);
  }

  private void processSubscriptionUpdated(String subscriptionId, JsonNode attributes) {
    String status = attributes.path(STATUS_FIELD).asText();
    updateSubscriptionStatus(subscriptionId, status, attributes);
    log.info("Updated subscription with id: {}", subscriptionId);
  }

  private void processSubscriptionCancelled(String subscriptionId, JsonNode attributes) {
    updateSubscriptionStatus(subscriptionId, "cancelled", attributes);
    log.info("Cancelled subscription with id: {}", subscriptionId);
  }

  private void processSubscriptionResumed(String subscriptionId, JsonNode attributes) {
    updateSubscriptionStatus(subscriptionId, "active", attributes);
    log.info("Resumed subscription with id: {}", subscriptionId);
  }

  private void processSubscriptionPaymentSuccess(String subscriptionId, JsonNode attributes) {
    updateSubscriptionStatus(subscriptionId, "active", attributes);
    log.info("Successful payment for subscription: {}", subscriptionId);
  }

  private void processSubscriptionPaymentFailed(String subscriptionId, JsonNode attributes) {
    updateSubscriptionStatus(subscriptionId, "past_due", attributes);
    log.info("Failed payment for subscription: {}", subscriptionId);
  }

  private void processSubscriptionPaymentRecovered(String subscriptionId, JsonNode attributes) {
    updateSubscriptionStatus(subscriptionId, "active", attributes);
    log.info("Recovered payment for subscription: {}", subscriptionId);
  }
}