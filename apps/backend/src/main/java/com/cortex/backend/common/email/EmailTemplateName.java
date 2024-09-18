package com.cortex.backend.common.email;


import lombok.Getter;

@Getter
public enum EmailTemplateName {
  ACTIVATE_ACCOUNT("activate_account"),
  RESET_PASSWORD("reset_password");

  EmailTemplateName(String name) {
    this.name = name;
  }

  private final String name;
}
