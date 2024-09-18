package com.cortex.backend.user.validations;

import com.cortex.backend.user.api.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CountryValidator implements ConstraintValidator<ValidCountry, String> {
  private final CountryService countryService;

  public CountryValidator(CountryService countryService) {
    this.countryService = countryService;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return false;
    }
    return countryService.getAllCountries().stream()
        .anyMatch(country -> country.getCode().equalsIgnoreCase(value));
  }
}