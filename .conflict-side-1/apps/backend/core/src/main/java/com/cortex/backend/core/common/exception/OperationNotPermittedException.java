package com.cortex.backend.core.common.exception;


public class OperationNotPermittedException extends RuntimeException {
  public OperationNotPermittedException(String msg) {
    super(msg);
  }
}
