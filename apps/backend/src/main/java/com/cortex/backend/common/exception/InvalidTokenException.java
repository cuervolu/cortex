package com.cortex.backend.common.exception;

public class InvalidTokenException  extends RuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }

}
