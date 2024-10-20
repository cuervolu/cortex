package com.cortex.backend.core.common.exception;

public class ExerciseReadException  extends RuntimeException {
  public ExerciseReadException(String message) {
    super(message);
  }

  public ExerciseReadException(String message, Throwable cause) {
    super(message, cause);
  }

}
