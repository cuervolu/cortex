package com.cortex.backend.core.common.exception;

public class NewPasswordDoesNotMatchException  extends RuntimeException {
  public NewPasswordDoesNotMatchException(String msg) {
    super(msg);
  }

}
