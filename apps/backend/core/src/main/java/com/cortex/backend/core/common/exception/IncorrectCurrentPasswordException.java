package com.cortex.backend.core.common.exception;

public class IncorrectCurrentPasswordException extends RuntimeException {

  public IncorrectCurrentPasswordException(String message) {
    super(message);
  }

}
