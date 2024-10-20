package com.cortex.backend.lemonsqueezy.subscriptions.invoices;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundRequest {
  private int amount;
}