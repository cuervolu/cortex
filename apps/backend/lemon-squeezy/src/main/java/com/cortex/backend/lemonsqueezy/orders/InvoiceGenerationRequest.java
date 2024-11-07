package com.cortex.backend.lemonsqueezy.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Request class for generating an order invoice in Lemon Squeezy.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceGenerationRequest {
  /**
   * The full name of the customer.
   */
  private String name;

  /**
   * The street address of the customer.
   */
  private String address;

  /**
   * The city of the customer.
   */
  private String city;

  /**
   * The state of the customer.
   */
  private String state;

  /**
   * The ZIP code of the customer.
   */
  @JsonProperty("zip_code")
  private String zipCode;

  /**
   * The country of the customer.
   */
  private String country;

  /**
   * Any additional notes to include on the invoice.
   */
  private String notes;
}