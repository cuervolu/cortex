package com.cortex.backend.lemonsqueezy.orders.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Represents a Lemon Squeezy order item object.
 * An order item represents a line item for an order that includes product, variant and price information.
 * An order item belongs to an Order and is associated with a Product and a Variant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

  /**
   * Unique identifier for the order item.
   */
  private String id;

  /**
   * The ID of the order this order item belongs to.
   */
  @JsonProperty("order_id")
  private long orderId;

  /**
   * The ID of the product associated with this order item.
   */
  @JsonProperty("product_id")
  private long productId;

  /**
   * The ID of the variant associated with this order item.
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
   * A positive integer in cents representing the price of this order item (in the order currency).
   * For "pay what you want" products the price will be whatever the customer entered at checkout.
   */
  private int price;

  /**
   * A positive integer representing the quantity of this order item.
   */
  private int quantity;

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
}