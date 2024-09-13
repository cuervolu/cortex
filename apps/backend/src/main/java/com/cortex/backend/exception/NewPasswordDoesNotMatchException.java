package com.cortex.backend.exception;

public class NewPasswordDoesNotMatchException  extends RuntimeException {
  public NewPasswordDoesNotMatchException(String msg) {
    super(msg);
  }

}
