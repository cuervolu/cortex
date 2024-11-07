package com.cortex.backend.lemonsqueezy.variants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * A variant represents a variation of a Product, with its own set of pricing options,
 * files and license key settings.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant {

  /**
   * Unique identifier for the variant
   */
  private String id;

  /**
   * The ID of the product this variant belongs to
   */
  @JsonProperty("product_id")
  private Long productId;

  /**
   * The name of the variant
   */
  private String name;

  /**
   * The slug used to identify the variant
   */
  private String slug;

  /**
   * The description of the variant in HTML
   */
  private String description;

  /**
   * Indicates if this variant should generate license keys for the customer on purchase
   */
  @JsonProperty("has_license_keys")
  private boolean hasLicenseKeys;

  /**
   * Maximum number of times a license key can be activated for this variant
   */
  @JsonProperty("license_activation_limit")
  private Integer licenseActivationLimit;

  /**
   * Indicates if license key activations are unlimited for this variant
   */
  @JsonProperty("is_license_limit_unlimited")
  private boolean isLicenseLimitUnlimited;

  /**
   * The number of units until a license key expires
   */
  @JsonProperty("license_length_value")
  private Integer licenseLengthValue;

  /**
   * The unit for license length (days, months, years)
   */
  @JsonProperty("license_length_unit")
  private String licenseLengthUnit;

  /**
   * Indicates if license keys should never expire
   */
  @JsonProperty("is_license_length_unlimited")
  private boolean isLicenseLengthUnlimited;

  /**
   * The sort order of this variant when displayed on the checkout
   */
  private Integer sort;

  /**
   * The status of the variant (pending, draft, published)
   */
  private String status;

  /**
   * The formatted status of the variant
   */
  @JsonProperty("status_formatted")
  private String statusFormatted;

  /**
   * Creation timestamp
   */
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  /**
   * Last update timestamp
   */
  @JsonProperty("updated_at")
  private ZonedDateTime updatedAt;

  /**
   * Indicates if the variant was created in test mode
   */
  @JsonProperty("test_mode")
  private boolean testMode;

  // Deprecated attributes included for backwards compatibility
  @JsonProperty("price")
  private Integer price;

  @JsonProperty("is_subscription")
  private boolean isSubscription;

  private String interval;

  @JsonProperty("interval_count")
  private Integer intervalCount;

  @JsonProperty("has_free_trial")
  private boolean hasFreeTrial;

  @JsonProperty("trial_interval")
  private String trialInterval;

  @JsonProperty("trial_interval_count")
  private Integer trialIntervalCount;

  @JsonProperty("pay_what_you_want")
  private boolean payWhatYouWant;

  @JsonProperty("min_price")
  private Integer minPrice;

  @JsonProperty("suggested_price")
  private Integer suggestedPrice;
}