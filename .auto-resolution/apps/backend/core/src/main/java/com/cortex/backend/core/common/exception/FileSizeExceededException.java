package com.cortex.backend.core.common.exception;

public class FileSizeExceededException extends RuntimeException {

  public FileSizeExceededException(String message) {
    super(message);
  }
}