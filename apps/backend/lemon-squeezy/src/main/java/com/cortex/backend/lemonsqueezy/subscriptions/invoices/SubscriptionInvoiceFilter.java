package com.cortex.backend.lemonsqueezy.subscriptions.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionInvoiceFilter {
  
  @JsonProperty("store_id")
  private Long storeId;
  private String status;
  private Boolean refunded;
  
  @JsonProperty("subscription_id")
  private Long subscriptionId;
}