package com.cortex.backend.lemonsqueezy.prices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Represents a price added to a Variant in Lemon Squeezy.
 * When a variant's price is changed a new price object is created. All old prices are retained.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

  /**
   * Unique identifier for the price
   */
  private String id;

  /**
   * The ID of the variant this price belongs to
   */
  @JsonProperty("variant_id")
  private Long variantId;

  /**
   * The type of variant this price was created for.
   * One of: one_time, subscription, lead_magnet, pwyw
   */
  private String category;

  /**
   * The pricing model for this price.
   * One of: standard, package, graduated, volume
   */
  private String scheme;

  /**
   * The type of usage aggregation for usage-based billing.
   * One of: sum, last_during_period, last_ever, max
   * Will be null if usage-based billing is not activated.
   */
  @JsonProperty("usage_aggregation")
  private String usageAggregation;

  /**
   * A positive integer in cents representing the price.
   * Not used for volume and graduated pricing.
   * Null if usage_aggregation is enabled.
   */
  @JsonProperty("unit_price")
  private Integer unitPrice;

  /**
   * A positive decimal string in cents representing the price.
   * Not used for volume and graduated pricing.
   * Null if usage_aggregation is not enabled.
   */
  @JsonProperty("unit_price_decimal")
  private String unitPriceDecimal;

  /**
   * Indicates if the price has a setup fee (subscription only)
   */
  @JsonProperty("setup_fee_enabled")
  private Boolean setupFeeEnabled;

  /**
   * A positive integer in cents representing the setup fee (subscription only)
   */
  @JsonProperty("setup_fee")
  private Integer setupFee;

  /**
   * The number of units included in each package when using package pricing.
   * Will be 1 for standard, graduated and volume pricing.
   */
  @JsonProperty("package_size")
  private Integer packageSize;

  /**
   * A list of pricing tier objects when using volume and graduated pricing.
   * Null for standard and package pricing.
   */
  private List<PriceTier> tiers;

  /**
   * The billing interval for subscriptions.
   * One of: day, week, month, year
   */
  @JsonProperty("renewal_interval_unit")
  private String renewalIntervalUnit;

  /**
   * The number of intervals between subscription billings
   */
  @JsonProperty("renewal_interval_quantity")
  private Integer renewalIntervalQuantity;

  /**
   * The interval unit of the free trial.
   * One of: day, week, month, year
   */
  @JsonProperty("trial_interval_unit")
  private String trialIntervalUnit;

  /**
   * The interval count of the free trial
   */
  @JsonProperty("trial_interval_quantity")
  private Integer trialIntervalQuantity;

  /**
   * Minimum price for PWYW products (in cents)
   */
  @JsonProperty("min_price")
  private Integer minPrice;

  /**
   * Suggested price for PWYW products (in cents)
   */
  @JsonProperty("suggested_price")
  private Integer suggestedPrice;

  /**
   * The product's tax category.
   * One of: eservice, ebook, saas
   */
  @JsonProperty("tax_code")
  private String taxCode;

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
   * Represents a pricing tier for volume and graduated pricing
   */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PriceTier {

    /**
     * The top limit of this tier.
     * Will be an integer or "inf" for infinite
     */
    @JsonProperty("last_unit")
    private String lastUnit;

    /**
     * Price per unit in cents
     */
    @JsonProperty("unit_price")
    private Integer unitPrice;

    /**
     * Decimal price per unit in cents
     */
    @JsonProperty("unit_price_decimal")
    private String unitPriceDecimal;

    /**
     * Optional fixed fee charged alongside the unit price
     */
    @JsonProperty("fixed_fee")
    private Integer fixedFee;
  }
}