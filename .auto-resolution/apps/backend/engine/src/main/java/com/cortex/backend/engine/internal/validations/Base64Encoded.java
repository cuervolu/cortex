package com.cortex.backend.engine.internal.validations;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = Base64EncodedValidator.class)
@Documented
public @interface Base64Encoded {
  String message() default "Must be Base64 encoded";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
