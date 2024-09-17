package com.cortex.backend.common.exception;

import com.cortex.backend.handler.BusinessErrorCodes;
import lombok.Getter;

@Getter
public class EmailSendingException extends RuntimeException {
  private final BusinessErrorCodes errorCode;

  public EmailSendingException(BusinessErrorCodes errorCode, Throwable cause) {
    super(errorCode.getDescription(), cause);
    this.errorCode = errorCode;
  }
}