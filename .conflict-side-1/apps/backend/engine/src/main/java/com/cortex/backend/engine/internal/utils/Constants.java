package com.cortex.backend.engine.internal.utils;

public final class Constants {

  private Constants() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static final String CODE_EXECUTION_QUEUE = "code_execution";
  public static final String RESULT_KEY_PREFIX = "result:";
  public static final long RESULT_EXPIRATION_HOURS = 1;
  public static final String VOLUME_PATH = "/code";
  public static final String EXERCISE_PATH = "/exercise";
  public static final String TMP_PATH = "/tmp";
  public static final String BIN_SH = "/bin/sh";

}
