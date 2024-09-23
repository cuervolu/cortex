package com.cortex.backend.common.exception;

public class ExpiredTokenException extends RuntimeException {
  public ExpiredTokenException(String message) {
    super(message);
  }
}