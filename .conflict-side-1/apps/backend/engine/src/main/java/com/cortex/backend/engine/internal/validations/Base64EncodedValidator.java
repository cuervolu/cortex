package com.cortex.backend.engine.internal.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Base64;

public class Base64EncodedValidator implements ConstraintValidator<Base64Encoded, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true; // Let @NotBlank handle this
    }
    try {
      Base64.getDecoder().decode(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}