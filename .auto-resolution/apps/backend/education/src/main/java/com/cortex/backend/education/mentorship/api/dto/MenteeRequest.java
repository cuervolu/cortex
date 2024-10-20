package com.cortex.backend.education.mentorship.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for mentee request")
public record MenteeRequest(
    @Schema(description = "Reason for requesting mentorship")
    @NotNull(message = "Reason is required")
    @NotBlank(message = "Reason cannot be blank")
    String reason
) {}
