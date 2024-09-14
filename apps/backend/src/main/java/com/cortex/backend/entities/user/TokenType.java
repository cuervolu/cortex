package com.cortex.backend.entities.user;

import lombok.Getter;

@Getter
public enum TokenType {
  ACTIVATION,
  PASSWORD_RESET
}