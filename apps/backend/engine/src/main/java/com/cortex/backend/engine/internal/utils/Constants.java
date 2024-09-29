package com.cortex.backend.engine.internal.utils;

public final class Constants {

  private Constants() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static final String CODE_EXECUTION_QUEUE = "code_execution";
  public static final String RESULT_KEY_PREFIX = "result:";
  public static final long RESULT_EXPIRATION_HOURS = 1;

}
