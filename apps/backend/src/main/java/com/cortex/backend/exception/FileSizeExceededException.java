package com.cortex.backend.exception;

public class FileSizeExceededException extends RuntimeException {

  public FileSizeExceededException(String message) {
    super(message);
  }
}