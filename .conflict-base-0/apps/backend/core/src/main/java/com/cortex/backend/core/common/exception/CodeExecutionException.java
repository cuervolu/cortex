package com.cortex.backend.core.common.exception;

public class CodeExecutionException extends Exception {

  public CodeExecutionException(String message) {
    super(message);
  }

  public CodeExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

}
