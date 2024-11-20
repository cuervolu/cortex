package com.cortex.backend.lemonsqueezy.subscriptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Represents a Lemon Squeezy subscription object.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Subscription {

  /**
   * Unique identifier for the subscription
   */
  private String id;

  /**
   * The ID of the store this subscription belongs to.
   */
  @JsonProperty("store_id")
  private long storeId;

  /**
   * The ID of the customer this subscription belongs to.
   */
  @JsonProperty("customer_id")
  private long customerId;

  /**
   * The ID of the order associated with this subscription.
   */
  @JsonProperty("order_id")
  private long orderId;

  /**
   * The ID of the order item associated with this subscription.
   */
  @JsonProperty("order_item_id")
  private long orderItemId;

  /**
   * The ID of the product associated with this subscription.
   */
  @JsonProperty("product_id")
  private long productId;

  /**
   * The ID of the variant associated with this subscription.
   */
  @JsonProperty("variant_id")
  private long variantId;

  /**
   * The name of the product.
   */
  @JsonProperty("product_name")
  private String productName;

  /**
   * The name of the variant.
   */
  @JsonProperty("variant_name")
  private String variantName;

  /**
   * The full name of the customer.
   */
  @JsonProperty("user_name")
  private String userName;

  /**
   * The email address of the customer.
   */
  @JsonProperty("user_email")
  private String userEmail;

  /**
   * The status of the subscription. One of: on_trial, active, paused, past_due, unpaid, cancelled,
   * expired
   */
  private String status;

  /**
   * The title-case formatted status of the subscription.
   */
  @JsonProperty("status_formatted")
  private String statusFormatted;

  /**
   * Lowercase brand of the card used to pay for the latest subscription payment.
   */
  @JsonProperty("card_brand")
  private String cardBrand;

  /**
   * The last 4 digits of the card used to pay for the latest subscription payment.
   */
  @JsonProperty("card_last_four")
  private String cardLastFour;

  /**
   * An object containing the payment collection pause behaviour options for the subscription, if
   * set.
   */
  private Map<String, Object> pause;

  /**
   * A boolean indicating if the subscription has been cancelled.
   */
  private boolean cancelled;

  /**
   * If the subscription has a free trial, this will be the date-time when the trial period ends.
   */
  @JsonProperty("trial_ends_at")
  private ZonedDateTime trialEndsAt;

  /**
   * An integer representing a day of the month on which subscription invoice payments are
   * collected.
   */
  @JsonProperty("billing_anchor")
  private int billingAnchor;

  /**
   * An object representing the first subscription item belonging to this subscription.
   */
  @JsonProperty("first_subscription_item")
  private SubscriptionItem firstSubscriptionItem;

  /**
   * An object of customer-facing URLs for managing the subscription.
   */
  private Map<String, String> urls;

  /**
   * The date-time indicating the end of the current billing cycle, and when the next invoice will
   * be issued.
   */
  @JsonProperty("renews_at")
  private ZonedDateTime renewsAt;

  /**
   * If the subscription has a status of cancelled or expired, this will be the date-time when the
   * subscription expires (or expired).
   */
  @JsonProperty("ends_at")
  private ZonedDateTime endsAt;

  /**
   * The date-time when the object was created.
   */
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  /**
   * The date-time when the object was last updated.
   */
  @JsonProperty("updated_at")
  private ZonedDateTime updatedAt;

  /**
   * A boolean indicating if the object was created within test mode.
   */
  @JsonProperty("test_mode")
  private boolean testMode;

  /**
   * Represents a subscription item.
   */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SubscriptionItem {

    private long id;
    @JsonProperty("subscription_id")
    private long subscriptionId;
    @JsonProperty("price_id")
    private long priceId;
    private int quantity;
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;
  }
}