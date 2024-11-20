package com.cortex.backend.lemonsqueezy.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Filter options for listing orders in Lemon Squeezy.
 */
@Data
@Builder
public class OrderFilter {
  /**
   * Only return orders belonging to the store with this ID.
   */
  @JsonProperty("store_id")
  private Long storeId;

  /**
   * Only return orders belonging to the customer with this ID.
   */
  @JsonProperty("customer_id")
  private Long customerId;

  /**
   * Only return orders with this user email.
   */
  
  @JsonProperty("user_email")
  private String userEmail;

  /**
   * Only return orders with this status.
   * One of: pending, failed, paid, refunded, partial_refund, fraudulent
   */
  private String status;
}