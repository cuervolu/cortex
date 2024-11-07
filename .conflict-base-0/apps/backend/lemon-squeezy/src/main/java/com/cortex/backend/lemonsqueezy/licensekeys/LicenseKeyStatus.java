package com.cortex.backend.lemonsqueezy.licensekeys;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LicenseKeyStatus {
  INACTIVE("inactive"),
  ACTIVE("active"),
  EXPIRED("expired"),
  DISABLED("disabled");

  private final String value;

  LicenseKeyStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}