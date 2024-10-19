package com.cortex.backend.lemonsqueezy.subscriptions.invoices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionInvoiceFilter {
  private Long storeId;
  private String status;
  private Boolean refunded;
  private Long subscriptionId;
}