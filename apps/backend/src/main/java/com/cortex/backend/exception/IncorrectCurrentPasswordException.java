package com.cortex.backend.exception;

public class IncorrectCurrentPasswordException extends RuntimeException {

  public IncorrectCurrentPasswordException(String message) {
    super(message);
  }

}
