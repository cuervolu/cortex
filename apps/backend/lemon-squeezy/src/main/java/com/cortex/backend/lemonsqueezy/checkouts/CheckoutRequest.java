package com.cortex.backend.lemonsqueezy.checkouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request to create a Lemon Squeezy checkout.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CheckoutRequest {

  /**
   * Custom price for the checkout in cents. For subscriptions, this price is used for all renewal
   * payments until the variant changes.
   */
  @JsonProperty("customer_price")
  private Integer customPrice;

  /**
   * Overridden product options for this checkout. Can include: name, description, media,
   * redirect_url, receipt_button_text, receipt_link_url, receipt_thank_you_note, enabled_variants.
   */
  @JsonProperty("product_options")
  private Map<String, Object> productOptions;

  /**
   * Checkout options for this checkout. Can include: embed, media, logo, desc, discount,
   * skip_trial, subscription_preview, and various color options for customizing the checkout page
   * appearance.
   */
  @JsonProperty("checkout_options")
  private Map<String, Object> checkoutOptions;

  /**
   * Prefill or custom data to be used in the checkout. Can include: email, name,
   * billing_address.country, billing_address.zip, tax_number, discount_code, custom data, and
   * variant_quantities.
   */
  @JsonProperty("checkout_data")
  private Map<String, Object> checkoutData;

  /**
   * Indicates whether to return a preview of the checkout. If true, the response will include a
   * preview object with checkout preview data.
   */
  private boolean preview;

  /**
   * Indicates whether the checkout should be created in test mode.
   */
  @JsonProperty("test_mode")
  private boolean testMode;

  /**
   * Expiration date and time of the checkout. Should be an ISO 8601 formatted date-time string. Can
   * be null for perpetual checkouts.
   */
  @JsonProperty("expires_at")
  private ZonedDateTime expiresAt;

  /**
   * ID of the store this checkout belongs to
   */
  @JsonProperty("store_id")
  private long storeId;

  /**
   * ID of the variant associated with this checkout
   */
  @JsonProperty("variant_id")
  private long variantId;
}