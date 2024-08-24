package com.cortex.backend.exception;


public class OperationNotPermittedException extends RuntimeException {
  public OperationNotPermittedException(String msg) {
    super(msg);
  }
}
