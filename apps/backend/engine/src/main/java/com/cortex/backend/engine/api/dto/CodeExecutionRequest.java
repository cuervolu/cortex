package com.cortex.backend.engine.api.dto;

import com.cortex.backend.engine.internal.validations.Base64Encoded;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CodeExecutionRequest(

    @Base64Encoded(message = "Code must be base64 encoded")
    @NotNull(message = "Code is required")
    @NotBlank(message = "Code must not be blank")
    String code,

    @NotNull(message = "Language is required")
    @NotBlank(message = "Language must not be blank")
    String language,

    @Positive(message = "Exercise ID must be positive")
    Long exerciseId
) {

}
