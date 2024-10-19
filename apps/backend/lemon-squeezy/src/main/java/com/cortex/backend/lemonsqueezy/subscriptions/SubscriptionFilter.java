package com.cortex.backend.lemonsqueezy.subscriptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionFilter {
  private Long storeId;
  private Long orderId;
  private Long orderItemId;
  private Long productId;
  private Long variantId;
  private String userEmail;
  private String status;
}