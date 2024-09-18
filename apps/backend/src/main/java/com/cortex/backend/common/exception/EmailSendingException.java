package com.cortex.backend.common.exception;

import lombok.Getter;

@Getter
public class EmailSendingException extends RuntimeException {
  public EmailSendingException(String message) {
    super(message);
  }
}