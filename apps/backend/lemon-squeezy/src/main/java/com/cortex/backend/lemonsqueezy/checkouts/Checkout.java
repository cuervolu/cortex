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
 * Represents a Lemon Squeezy checkout object.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Checkout {

  /**
   * Unique identifier for the checkout
   */
  private String id;

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

  /**
   * Custom price for the checkout in cents
   */
  @JsonProperty("customer_price")
  private Integer customPrice;

  /**
   * Overridden product options for this checkout
   */
  @JsonProperty("product_options")
  private Map<String, Object> productOptions;

  /**
   * Checkout options for this checkout
   */
  @JsonProperty("checkout_options")
  private Map<String, Object> checkoutOptions;

  /**
   * Prefill or custom data used in the checkout
   */
  @JsonProperty("checkout_data")
  private Map<String, Object> checkoutData;

  /**
   * Expiration date and time of the checkout
   */
  @JsonProperty("expires_at")
  private ZonedDateTime expiresAt;

  /**
   * Creation date and time of the checkout
   */
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  /**
   * Last update date and time of the checkout
   */
  @JsonProperty("updated_at")
  private ZonedDateTime updatedAt;

  /**
   * Indicates if the checkout is in test mode
   */
  @JsonProperty("test_mode")
  private boolean testMode;

  /**
   * URL of the checkout
   */
  private String url;
}
