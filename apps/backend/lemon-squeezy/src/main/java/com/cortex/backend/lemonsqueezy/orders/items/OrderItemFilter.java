package com.cortex.backend.lemonsqueezy.orders.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Filter options for listing order items in Lemon Squeezy.
 */
@Data
@Builder
public class OrderItemFilter {

  /**
   * Only return order items belonging to the order with this ID.
   */
  @JsonProperty("order_id")
  private Long orderId;

  /**
   * Only return order items associated with the product with this ID.
   */
  @JsonProperty("product_id")
  private Long productId;

  /**
   * Only return order items associated with the variant with this ID.
   */
  @JsonProperty("variant_id")
  private Long variantId;
}