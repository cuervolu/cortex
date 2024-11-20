package com.cortex.backend.core.common.exception;

import com.cortex.backend.core.common.BusinessErrorCodes;
import lombok.Getter;

@Getter
public class InvalidExerciseStateException extends RuntimeException {
  private final BusinessErrorCodes errorCode;

  public InvalidExerciseStateException(String message, BusinessErrorCodes errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
}