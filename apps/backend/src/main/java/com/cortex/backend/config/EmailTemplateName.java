package com.cortex.backend.config;


import lombok.Getter;

@Getter
public enum EmailTemplateName {
  ACTIVATE_ACCOUNT("activate_account");

  EmailTemplateName(String name) {
    this.name = name;
  }

  private final String name;
}
