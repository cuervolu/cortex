package com.cortex.backend.payments.internal;

import com.cortex.backend.core.common.exception.PaymentServiceException;
import com.cortex.backend.core.domain.Subscription;
import com.cortex.backend.core.domain.WebhookEvent;
import com.cortex.backend.lemonsqueezy.prices.Price;
import com.cortex.backend.lemonsqueezy.prices.PriceService;
import com.cortex.backend.lemonsqueezy.webhook.WebhookEventType;
import com.cortex.backend.payments.api.SubscriptionRepository;
import com.cortex.backend.payments.api.WebhookEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookHandler {

  private final WebhookEventRepository webhookEventRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final WebhookEventHandler webhookEventHandler;
  private final PriceService priceService;
  private final ObjectMapper objectMapper;

  @Value("${lemon-squeezy.webhook.secret}")
  private String signingSecret;

  private static final String HMAC_SHA256 = "HmacSHA256";

  @Transactional
  public boolean processWebhook(String payload, String signature) {
    if (!isValidSignature(payload, signature)) {
      log.warn("Invalid webhook signature received");
      return false;
    }

    try {
      JsonNode jsonNode = objectMapper.readTree(payload);
      String eventName = jsonNode.path("meta").path("event_name").asText();

      WebhookEvent webhookEvent = WebhookEvent.builder()
          .eventName(eventName)
          .body(payload)
          .processed(false)
          .createdAt(LocalDateTime.now())
          .build();

      webhookEvent = webhookEventRepository.save(webhookEvent);

      if (WebhookEventType.isSubscriptionEvent(eventName)) {
        processSubscriptionWebhook(jsonNode.get("data"), jsonNode.get("meta"));
      } else {
        processPaymentWebhook(webhookEvent);
      }

      webhookEvent.setProcessed(true);
      webhookEventRepository.save(webhookEvent);
      return true;

    } catch (Exception e) {
      log.error("Error processing webhook payload", e);
      return false;
    }
  }

  private void processPaymentWebhook(WebhookEvent event) throws IOException {
    JsonNode jsonNode = objectMapper.readTree(event.getBody());
    String eventName = jsonNode.path("meta").path("event_name").asText();

    WebhookEventType eventType = WebhookEventType.fromString(eventName);
    webhookEventHandler.handleWebhookEvent(eventType, jsonNode);
  }

  public void processSubscriptionWebhook(JsonNode data, JsonNode meta) {
    try {
      JsonNode attributes = data.get("attributes");
      if (attributes == null) {
        log.error("No attributes found in webhook data");
        return;
      }

      String subscriptionId = data.get("id").asText();
      String userId = meta.path("custom_data").path("user_id").asText();

      Subscription subscription = subscriptionRepository
          .findByLemonSqueezyId(subscriptionId)
          .orElseGet(() -> Subscription.builder()
              .lemonSqueezyId(subscriptionId)
              .build());

      updateSubscriptionFromWebhook(subscription, attributes, userId);
      subscriptionRepository.save(subscription);

      log.info("Successfully processed subscription webhook for ID: {}", subscriptionId);

    } catch (Exception e) {
      log.error("Error processing subscription webhook", e);
      throw new PaymentServiceException("Failed to process subscription webhook", e);
    }
  }

  private void updateSubscriptionFromWebhook(Subscription subscription, JsonNode attributes, String userId)
      throws IOException {
    subscription.setOrderId(attributes.get("order_id").asLong());
    subscription.setName(attributes.get("user_name").asText());
    subscription.setEmail(attributes.get("user_email").asText());
    subscription.setStatus(attributes.get("status").asText());
    subscription.setStatusFormatted(attributes.get("status_formatted").asText());
    subscription.setUserId(userId);

    setDateIfPresent(subscription, attributes, "renews_at");
    setDateIfPresent(subscription, attributes, "ends_at");
    setDateIfPresent(subscription, attributes, "trial_ends_at");

    JsonNode firstItem = attributes.get("first_subscription_item");
    if (firstItem != null) {
      long subscriptionItemId = firstItem.get("id").asLong();
      subscription.setSubscriptionItemId(subscriptionItemId);
      subscription.setUsageBased(firstItem.get("is_usage_based").asBoolean());

      long priceId = firstItem.get("price_id").asLong();
      Price price = priceService.getPrice(String.valueOf(priceId));
      String priceValue = subscription.isUsageBased() ?
          price.getUnitPriceDecimal() :
          String.valueOf(price.getUnitPrice());
      subscription.setPrice(priceValue);
    }

    JsonNode pauseNode = attributes.get("pause");
    subscription.setPaused(pauseNode != null && !pauseNode.isEmpty());
  }

  private void setDateIfPresent(Subscription subscription, JsonNode attributes, String fieldName) {
    JsonNode dateNode = attributes.get(fieldName);
    if (dateNode != null && !dateNode.isNull()) {
      String dateStr = ZonedDateTime.parse(dateNode.asText()).toString();
      switch (fieldName) {
        case "renews_at" -> subscription.setRenewsAt(dateStr);
        case "ends_at" -> subscription.setEndsAt(dateStr);
        case "trial_ends_at" -> subscription.setTrialEndsAt(dateStr);
        default -> log.warn("Unknown date field: {}", fieldName);
      }
    }
  }

  private boolean isValidSignature(String payload, String signature) {
    if (signature == null || signature.isEmpty()) {
      return false;
    }

    try {
      Mac sha256Hmac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec secretKey = new SecretKeySpec(
          signingSecret.getBytes(StandardCharsets.UTF_8),
          HMAC_SHA256
      );
      sha256Hmac.init(secretKey);

      byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
      String calculatedSignature = bytesToHex(hash);

      return signature.equals(calculatedSignature);

    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      log.error("Error validating webhook signature", e);
      return false;
    }
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
