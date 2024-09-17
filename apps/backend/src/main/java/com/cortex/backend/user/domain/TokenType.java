package com.cortex.backend.user.domain;

import lombok.Getter;

@Getter
public enum TokenType {
  ACTIVATION,
  PASSWORD_RESET
}