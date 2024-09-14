package com.cortex.backend.exception;

public class InvalidTokenException  extends RuntimeException {

  public InvalidTokenException(String message) {
    super(message);
  }

}
