package com.cortex.backend.lemonsqueezy.subscriptions.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Represents a Lemon Squeezy subscription invoice object.
 */
@Data
public class SubscriptionInvoice {

  /**
   * Unique identifier for the subscription invoice
   */
  private String id;

  /**
   * The ID of the Store this subscription invoice belongs to
   */
  @JsonProperty("store_id")
  private long storeId;

  /**
   * The ID of the Subscription associated with this subscription invoice
   */
  @JsonProperty("subscription_id")
  private long subscriptionId;

  /**
   * The ID of the customer this subscription invoice belongs to
   */
  @JsonProperty("customer_id")
  private long customerId;

  /**
   * The full name of the customer
   */
  @JsonProperty("user_name")
  private String userName;

  /**
   * The email address of the customer
   */
  @JsonProperty("user_email")
  private String userEmail;

  /**
   * The reason for the invoice being generated. Possible values:
   * - initial: The initial invoice generated when the subscription is created.
   * - renewal: A renewal invoice generated when the subscription is renewed.
   * - updated: An invoice generated when the subscription is updated.
   */
  @JsonProperty("billing_reason")
  private String billingReason;

  /**
   * Lowercase brand of the card used to pay for the invoice. One of: visa, mastercard, amex,
   * discover, jcb, diners, unionpay. Will be empty for non-card payments.
   */
  @JsonProperty("card_brand")
  private String cardBrand;

  /**
   * The last 4 digits of the card used to pay for the invoice. Will be empty for non-card
   * payments.
   */
  @JsonProperty("card_last_four")
  private String cardLastFour;

  /**
   * The ISO 4217 currency code for the invoice (e.g. USD, GBP, etc.)
   */
  private String currency;

  /**
   * If the invoice currency is USD, this will always be 1.0. Otherwise, this is the currency
   * conversion rate used to determine the cost of the invoice in USD at the time of payment.
   */
  @JsonProperty("currency_rate")
  private String currencyRate;

  /**
   * The status of the invoice. One of:
   * - pending: The invoice is waiting for payment.
   * - paid: The invoice has been paid.
   * - void: The invoice was cancelled or cannot be paid.
   * - refunded: The invoice was paid but has since been fully refunded.
   * - partial_refund: The invoice was paid but has since been partially refunded.
   */
  private String status;

  /**
   * The formatted status of the invoice
   */
  @JsonProperty("status_formatted")
  private String statusFormatted;

  /**
   * A boolean value indicating whether the invoice has been fully refunded
   */
  private boolean refunded;

  /**
   * If the invoice has been fully refunded, this will be an ISO 8601 formatted date-time string
   * indicating when the invoice was refunded. Otherwise, it will be null.
   */
  @JsonProperty("refunded_at")
  private ZonedDateTime refundedAt;

  /**
   * A positive integer in cents representing the subtotal of the invoice in the invoice currency
   */
  private int subtotal;

  /**
   * A positive integer in cents representing the total discount value applied to the invoice in the
   * invoice currency
   */
  @JsonProperty("discount_total")
  private int discountTotal;

  /**
   * A positive integer in cents representing the tax applied to the invoice in the invoice
   * currency
   */
  private int tax;

  /**
   * A boolean indicating if the order was created with tax inclusive or exclusive pricing
   */
  @JsonProperty("tax_inclusive")
  private boolean taxInclusive;

  /**
   * A positive integer in cents representing the total cost of the invoice in the invoice currency
   */
  private int total;

  /**
   * A positive integer in cents representing the refunded amount of the invoice in the invoice
   * currency
   */
  @JsonProperty("refunded_amount")
  private int refundedAmount;

  /**
   * A positive integer in cents representing the subtotal of the invoice in USD
   */
  @JsonProperty("subtotal_usd")
  private int subtotalUsd;

  /**
   * A positive integer in cents representing the total discount value applied to the invoice in
   * USD
   */
  @JsonProperty("discount_total_usd")
  private int discountTotalUsd;

  /**
   * A positive integer in cents representing the tax applied to the invoice in USD
   */
  @JsonProperty("tax_usd")
  private int taxUsd;

  /**
   * A positive integer in cents representing the total cost of the invoice in USD
   */
  @JsonProperty("total_usd")
  private int totalUsd;

  /**
   * A positive integer in cents representing the refunded amount of the invoice in USD
   */
  @JsonProperty("refunded_amount_usd")
  private int refundedAmountUsd;

  /**
   * A human-readable string representing the subtotal of the invoice in the invoice currency (e.g.
   * $9.99)
   */
  @JsonProperty("subtotal_formatted")
  private String subtotalFormatted;

  /**
   * A human-readable string representing the total discount value applied to the invoice in the
   * invoice currency (e.g. $9.99)
   */
  @JsonProperty("discount_total_formatted")
  private String discountTotalFormatted;

  /**
   * A human-readable string representing the tax applied to the invoice in the invoice currency
   * (e.g. $9.99)
   */
  @JsonProperty("tax_formatted")
  private String taxFormatted;

  /**
   * A human-readable string representing the total cost of the invoice in the invoice currency
   * (e.g. $9.99)
   */
  @JsonProperty("total_formatted")
  private String totalFormatted;

  /**
   * A human-readable string representing the refunded amount of the invoice in the invoice currency
   * (e.g. $9.99)
   */
  @JsonProperty("refunded_amount_formatted")
  private String refundedAmountFormatted;

  /**
   * An object of customer-facing URLs for the invoice. It contains:
   * - invoice_url: The unique URL to download a PDF of the invoice. Note: for security reasons,
   * download URLs are signed (but do not expire). Will be null if status is pending.
   */
  private Map<String, String> urls;

  /**
   * An ISO 8601 formatted date-time string indicating when the invoice was created
   */
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  /**
   * An ISO 8601 formatted date-time string indicating when the invoice was last updated
   */
  @JsonProperty("updated_at")
  private ZonedDateTime updatedAt;

  /**
   * A boolean indicating if the object was created within test mode
   */
  @JsonProperty("test_mode")
  private boolean testMode;
}