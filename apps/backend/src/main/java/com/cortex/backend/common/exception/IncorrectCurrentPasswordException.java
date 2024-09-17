package com.cortex.backend.common.exception;

public class IncorrectCurrentPasswordException extends RuntimeException {

  public IncorrectCurrentPasswordException(String message) {
    super(message);
  }

}
