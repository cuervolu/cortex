package com.cortex.backend.lemonsqueezy.webhook;

import lombok.Getter;

/**
 * Enumeration of all possible webhook event types from Lemon Squeezy.
 * These events can be used to configure webhooks and handle incoming webhook payloads.
 */
@Getter
public enum WebhookEventType {
  // Order related events
  ORDER_CREATED("order_created", "Fired when an order is created"),
  ORDER_REFUNDED("order_refunded", "Fired when an order is refunded"),

  // Subscription lifecycle events
  SUBSCRIPTION_CREATED("subscription_created", "Fired when a subscription is created"),
  SUBSCRIPTION_UPDATED("subscription_updated", "Fired when a subscription is updated"),
  SUBSCRIPTION_CANCELLED("subscription_cancelled", "Fired when a subscription is cancelled"),
  SUBSCRIPTION_RESUMED("subscription_resumed", "Fired when a cancelled subscription is resumed"),
  SUBSCRIPTION_EXPIRED("subscription_expired", "Fired when a subscription expires"),
  SUBSCRIPTION_PAUSED("subscription_paused", "Fired when a subscription is paused"),
  SUBSCRIPTION_UNPAUSED("subscription_unpaused", "Fired when a subscription is unpaused"),

  // Subscription payment events
  SUBSCRIPTION_PAYMENT_FAILED("subscription_payment_failed", "Fired when a subscription payment fails"),
  SUBSCRIPTION_PAYMENT_SUCCESS("subscription_payment_success", "Fired when a subscription payment is successful"),
  SUBSCRIPTION_PAYMENT_RECOVERED("subscription_payment_recovered", "Fired when a failed subscription payment is recovered"),
  SUBSCRIPTION_PAYMENT_REFUNDED("subscription_payment_refunded", "Fired when a subscription payment is refunded"),
  SUBSCRIPTION_PLAN_CHANGED("subscription_plan_changed", "Fired when a subscription's plan is changed"),

  // License key events
  LICENSE_KEY_CREATED("license_key_created", "Fired when a license key is created"),
  LICENSE_KEY_UPDATED("license_key_updated", "Fired when a license key is updated");

  /**
   * -- GETTER --
   *  Returns the event name as used in the Lemon Squeezy API.
   *
   */
  private final String eventName;
  /**
   * -- GETTER --
   *  Returns a human-readable description of when this event is fired.
   *
   */
  private final String description;

  WebhookEventType(String eventName, String description) {
    this.eventName = eventName;
    this.description = description;
  }

  /**
   * Converts a string event name to its corresponding WebhookEventType.
   *
   * @param eventName the event name from the webhook payload
   * @return the corresponding WebhookEventType
   * @throws IllegalArgumentException if the event name is not recognized
   */
  public static WebhookEventType fromString(String eventName) {
    for (WebhookEventType type : WebhookEventType.values()) {
      if (type.eventName.equals(eventName)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown webhook event type: " + eventName);
  }

  /**
   * Checks if the given event name is a valid Lemon Squeezy webhook event.
   *
   * @param eventName the event name to validate
   * @return true if the event name is valid, false otherwise
   */
  public static boolean isValidEventName(String eventName) {
    for (WebhookEventType type : WebhookEventType.values()) {
      if (type.eventName.equals(eventName)) {
        return true;
      }
    }
    return false;
  }

    public static boolean isSubscriptionEvent(String eventName) {
    return eventName.startsWith("subscription_");
  }

  /**
   * Groups the event type into its category.
   *
   * @return the category of this event type
   */
  public EventCategory getCategory() {
    if (eventName.startsWith("order_")) {
      return EventCategory.ORDER;
    } else if (eventName.startsWith("subscription_payment_")) {
      return EventCategory.SUBSCRIPTION_PAYMENT;
    } else if (eventName.startsWith("subscription_")) {
      return EventCategory.SUBSCRIPTION;
    } else if (eventName.startsWith("license_key_")) {
      return EventCategory.LICENSE_KEY;
    }
    throw new IllegalStateException("Unknown category for event: " + eventName);
  }

  /**
   * Categories of webhook events.
   */
  public enum EventCategory {
    ORDER,
    SUBSCRIPTION,
    SUBSCRIPTION_PAYMENT,
    LICENSE_KEY
  }
}