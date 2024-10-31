package com.cortex.backend.lemonsqueezy.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Represents a Lemon Squeezy order object.
 * An order is created when a customer purchases a product.
 * An order belongs to a Store, is associated with a Customer and can have many Order Items,
 * Subscriptions, License Keys and Discount Redemptions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  /**
   * Unique identifier for the order.
   */
  private String id;

  /**
   * The ID of the store this order belongs to.
   */
  @JsonProperty("store_id")
  private long storeId;

  /**
   * The ID of the customer this order belongs to.
   */
  @JsonProperty("customer_id")
  private long customerId;

  /**
   * The unique identifier (UUID) for this order.
   */
  private String identifier;

  /**
   * An integer representing the sequential order number for this store.
   */
  @JsonProperty("order_number")
  private int orderNumber;

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
   * The ISO 4217 currency code for the order (e.g. USD, GBP, etc).
   */
  private String currency;

  /**
   * If the order currency is USD, this will always be 1.0.
   * Otherwise, this is the currency conversion rate used to determine the cost of the order
   * in USD at the time of purchase.
   */
  @JsonProperty("currency_rate")
  private String currencyRate;

  /**
   * A positive integer in cents representing the subtotal of the order in the order currency.
   */
  private int subtotal;

  /**
   * A positive integer in cents representing the setup fee of the order in the order currency.
   */
  @JsonProperty("setup_fee")
  private int setupFee;

  /**
   * A positive integer in cents representing the total discount value applied to the order
   * in the order currency.
   */
  @JsonProperty("discount_total")
  private int discountTotal;

  /**
   * A positive integer in cents representing the tax applied to the order in the order currency.
   */
  private int tax;

  /**
   * A positive integer in cents representing the total cost of the order in the order currency.
   */
  private int total;

  /**
   * A positive integer in cents representing the refunded amount of the order in the order currency.
   */
  @JsonProperty("refunded_amount")
  private int refundedAmount;

  /**
   * A positive integer in cents representing the subtotal of the order in USD.
   */
  @JsonProperty("subtotal_usd")
  private int subtotalUsd;

  /**
   * A positive integer in cents representing the setup fee of the order in USD.
   */
  @JsonProperty("setup_fee_usd")
  private int setupFeeUsd;

  /**
   * A positive integer in cents representing the total discount value applied to the order in USD.
   */
  @JsonProperty("discount_total_usd")
  private int discountTotalUsd;

  /**
   * A positive integer in cents representing the tax applied to the order in USD.
   */
  @JsonProperty("tax_usd")
  private int taxUsd;

  /**
   * A positive integer in cents representing the total cost of the order in USD.
   */
  @JsonProperty("total_usd")
  private int totalUsd;

  /**
   * A positive integer in cents representing the refunded amount of the order in USD.
   */
  @JsonProperty("refunded_amount_usd")
  private int refundedAmountUsd;

  /**
   * The name of the tax rate (e.g. VAT, Sales Tax, etc) applied to the order.
   * Will be null if no tax was applied.
   */
  @JsonProperty("tax_name")
  private String taxName;

  /**
   * If tax is applied to the order, this will be the rate of tax as a decimal percentage.
   */
  @JsonProperty("tax_rate")
  private String taxRate;

  /**
   * A boolean indicating if the order was created with tax inclusive or exclusive pricing.
   */
  @JsonProperty("tax_inclusive")
  private boolean taxInclusive;

  /**
   * The status of the order. One of:
   * - pending
   * - failed
   * - paid
   * - refunded
   * - partial_refund
   * - fraudulent
   */
  private String status;

  /**
   * The formatted status of the order.
   */
  @JsonProperty("status_formatted")
  private String statusFormatted;

  /**
   * Has the value true if the order has been fully refunded.
   */
  private boolean refunded;

  /**
   * If the order has been fully refunded, this will be an ISO 8601 formatted date-time string
   * indicating when the order was refunded.
   */
  @JsonProperty("refunded_at")
  private ZonedDateTime refundedAt;

  /**
   * A human-readable string representing the subtotal of the order in the order currency (e.g. $9.99).
   */
  @JsonProperty("subtotal_formatted")
  private String subtotalFormatted;

  /**
   * A human-readable string representing the setup fee of the order in the order currency (e.g. $9.99).
   */
  @JsonProperty("setup_fee_formatted")
  private String setupFeeFormatted;

  /**
   * A human-readable string representing the total discount value applied to the order
   * in the order currency (e.g. $9.99).
   */
  @JsonProperty("discount_total_formatted")
  private String discountTotalFormatted;

  /**
   * A human-readable string representing the tax applied to the order in the order currency (e.g. $9.99).
   */
  @JsonProperty("tax_formatted")
  private String taxFormatted;

  /**
   * A human-readable string representing the total cost of the order in the order currency (e.g. $9.99).
   */
  @JsonProperty("total_formatted")
  private String totalFormatted;

  /**
   * A human-readable string representing the refunded amount of the order in the order currency (e.g. $9.99).
   */
  @JsonProperty("refunded_amount_formatted")
  private String refundedAmountFormatted;

  /**
   * An object representing the first Order Item belonging to this order.
   */
  @JsonProperty("first_order_item")
  private OrderItem firstOrderItem;

  /**
   * An object of customer-facing URLs for this order.
   * Contains:
   * - receipt: A pre-signed URL for viewing the order in the customer's My Orders page.
   */
  private Map<String, String> urls;

  /**
   * An ISO 8601 formatted date-time string indicating when the object was created.
   */
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  /**
   * An ISO 8601 formatted date-time string indicating when the object was last updated.
   */
  @JsonProperty("updated_at")
  private ZonedDateTime updatedAt;

  /**
   * A boolean indicating if the object was created within test mode.
   */
  @JsonProperty("test_mode")
  private boolean testMode;

  /**
   * Represents an order item within a Lemon Squeezy order.
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class OrderItem {
    private long id;

    @JsonProperty("order_id")
    private long orderId;

    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("variant_id")
    private long variantId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("variant_name")
    private String variantName;

    private int price;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("test_mode")
    private boolean testMode;
  }
}