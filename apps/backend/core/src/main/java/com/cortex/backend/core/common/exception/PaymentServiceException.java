package com.cortex.backend.core.common.exception;

public class PaymentServiceException extends RuntimeException {
  public PaymentServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}