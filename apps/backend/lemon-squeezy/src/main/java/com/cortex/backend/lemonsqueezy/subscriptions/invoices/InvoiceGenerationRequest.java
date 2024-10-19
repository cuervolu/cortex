package com.cortex.backend.lemonsqueezy.subscriptions.invoices;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceGenerationRequest {
  private String name;
  private String address;
  private String city;
  private String state;
  private String zipCode;
  private String country;
  private String notes;
}