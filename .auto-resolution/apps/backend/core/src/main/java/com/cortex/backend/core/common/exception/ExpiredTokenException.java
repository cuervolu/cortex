package com.cortex.backend.core.common.exception;

public class ExpiredTokenException extends RuntimeException {
  public ExpiredTokenException(String message) {
    super(message);
  }
}