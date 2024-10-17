package com.cortex.backend.payments.model;

import lombok.Data;

@Data
public class PaymentMethodsAllowed {

  private PaymentType[] paymentTypes;
  private PaymentMethod[] paymentMethods;

  @Data
  public static class PaymentType {

    private String id;
  }

  @Data
  public static class PaymentMethod {

    private String id;
  }
}