package com.cortex.backend.lemonsqueezy.subscriptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionFilter {
  @JsonProperty("store_id")
  private Long storeId;
  
  @JsonProperty("order_id")
  private Long orderId;
  
  @JsonProperty("order_item_id")
  private Long orderItemId;
  
  @JsonProperty("product_id")
  private Long productId;
  
  @JsonProperty("variant_id")
  private Long variantId;
  
  @JsonProperty("user_email")
  private String userEmail;
  private String status;
}