package com.cortex.backend.education.mentorship.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for mentorship request")
public record MentorApplicationRequest(
    @Schema(description = "Area of expertise", example = "Java Programming")
    @NotNull(message = "Area of expertise is required")
    @NotBlank(message = "Area of expertise cannot be blank")
    String area,

    @Schema(description = "Reason for wanting to become a mentor")
    @NotNull(message = "Reason is required")
    @NotBlank(message = "Reason cannot be blank")
    String reason
) {

}
