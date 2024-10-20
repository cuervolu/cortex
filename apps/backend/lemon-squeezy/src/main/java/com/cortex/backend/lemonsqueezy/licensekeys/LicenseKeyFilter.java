package com.cortex.backend.lemonsqueezy.licensekeys;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LicenseKeyFilter {
  private Long storeId;
  private Long orderId;
  private Long orderItemId;
  private Long productId;
  private String status;
}