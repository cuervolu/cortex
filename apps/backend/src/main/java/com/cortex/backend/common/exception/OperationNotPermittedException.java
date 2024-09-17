package com.cortex.backend.common.exception;


public class OperationNotPermittedException extends RuntimeException {
  public OperationNotPermittedException(String msg) {
    super(msg);
  }
}
