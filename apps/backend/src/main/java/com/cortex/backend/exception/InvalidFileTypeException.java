package com.cortex.backend.exception;

public class InvalidFileTypeException extends RuntimeException {
  public InvalidFileTypeException(String message) {
    super(message);
  }
}
