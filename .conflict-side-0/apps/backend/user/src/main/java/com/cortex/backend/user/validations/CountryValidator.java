package com.cortex.backend.user.validations;

import com.cortex.backend.user.api.CountryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CountryValidator implements ConstraintValidator<ValidCountry, String> {
  private static final Logger logger = LoggerFactory.getLogger(CountryValidator.class);
  private final CountryService countryService;
  private boolean allowNull;

  public CountryValidator(CountryService countryService) {
    this.countryService = countryService;
  }

  @Override
  public void initialize(ValidCountry constraintAnnotation) {
    this.allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return allowNull;
    }

    return countryService.getAllCountries().stream()
        .anyMatch(country -> {
          try {
            Method getCodeMethod = country.getClass().getMethod("getCode");
            String code = (String) getCodeMethod.invoke(country);
            return code.equalsIgnoreCase(value);
          } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error validating country code: ", e);
            return false;
          }
        });
  }
}