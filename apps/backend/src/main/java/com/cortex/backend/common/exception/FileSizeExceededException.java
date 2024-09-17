package com.cortex.backend.common.exception;

public class FileSizeExceededException extends RuntimeException {

  public FileSizeExceededException(String message) {
    super(message);
  }
}