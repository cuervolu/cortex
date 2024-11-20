package com.cortex.backend.lemonsqueezy.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Products describe digital goods you offer to your customers.
 * A product belongs to a Store and can have many Variants.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  /**
   * Unique identifier for the product
   */
  private String id;

  /**
   * The ID of the store this product belongs to.
   */
  @JsonProperty("store_id")
  private long storeId;

  /**
   * The name of the product.
   */
  private String name;

  /**
   * The slug used to identify the product.
   */
  private String slug;

  /**
   * The description of the product in HTML.
   */
  private String description;

  /**
   * The status of the product. Either draft or published.
   */
  private String status;

  /**
   * The formatted status of the product.
   */
  @JsonProperty("status_formatted")
  private String statusFormatted;

  /**
   * A URL to the thumbnail image for this product (if one exists).
   */
  @JsonProperty("thumb_url")
  private String thumbUrl;

  /**
   * A URL to the large thumbnail image for this product (if one exists).
   */
  @JsonProperty("large_thumb_url")
  private String largeThumbUrl;

  /**
   * A positive integer in cents representing the price of the product.
   */
  private int price;

  /**
   * A human-readable string representing the price of the product (e.g. $9.99).
   */
  @JsonProperty("price_formatted")
  private String priceFormatted;

  /**
   * The price of the cheapest variant (if multiple variants exist).
   */
  @JsonProperty("from_price")
  private Integer fromPrice;

  /**
   * The formatted price of the cheapest variant.
   */
  @JsonProperty("from_price_formatted")
  private String fromPriceFormatted;

  /**
   * The price of the most expensive variant (if multiple variants exist).
   */
  @JsonProperty("to_price")
  private Integer toPrice;

  /**
   * The formatted price of the most expensive variant.
   */
  @JsonProperty("to_price_formatted")
  private String toPriceFormatted;

  /**
   * Indicates if this is a "pay what you want" product.
   */
  @JsonProperty("pay_what_you_want")
  private boolean payWhatYouWant;

  /**
   * A URL to purchase this product using the Lemon Squeezy checkout.
   */
  @JsonProperty("buy_now_url")
  private String buyNowUrl;

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
   * Indicates if the product was created in test mode
   */
  @JsonProperty("test_mode")
  private boolean testMode;
}