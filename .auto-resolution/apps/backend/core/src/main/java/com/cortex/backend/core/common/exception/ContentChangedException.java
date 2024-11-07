package com.cortex.backend.core.common.exception;

public class ContentChangedException extends RuntimeException {

  public ContentChangedException(String message) {
    super(message);
  }

  public ContentChangedException(String message, Throwable cause) {
    super(message, cause);
  }
}