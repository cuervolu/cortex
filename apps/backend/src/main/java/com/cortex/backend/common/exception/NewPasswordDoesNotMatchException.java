package com.cortex.backend.common.exception;

public class NewPasswordDoesNotMatchException  extends RuntimeException {
  public NewPasswordDoesNotMatchException(String msg) {
    super(msg);
  }

}
