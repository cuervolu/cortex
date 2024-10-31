package com.cortex.backend.lemonsqueezy.licensekeys;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LicenseKeyFilter {

  @JsonProperty("store_id")
  private Long storeId;

  @JsonProperty("order_id")
  private Long orderId;

  @JsonProperty("order_item_id")
  private Long orderItemId;

  @JsonProperty("product_id")
  private Long productId;
  private String status;
}