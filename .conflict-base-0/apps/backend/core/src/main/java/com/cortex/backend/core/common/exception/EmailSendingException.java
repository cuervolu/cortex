package com.cortex.backend.core.common.exception;

import lombok.Getter;

@Getter
public class EmailSendingException extends RuntimeException {
  public EmailSendingException(String message) {
    super(message);
  }
}