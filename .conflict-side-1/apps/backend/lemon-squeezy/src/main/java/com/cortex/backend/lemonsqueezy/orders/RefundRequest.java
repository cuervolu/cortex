package com.cortex.backend.lemonsqueezy.orders;

import lombok.Builder;
import lombok.Data;

/**
 * Request class for issuing a refund in Lemon Squeezy.
 */
@Data
@Builder
public class RefundRequest {
  /**
   * The amount to refund in cents.
   * If not provided, a full refund will be issued.
   */
  private Integer amount;
}