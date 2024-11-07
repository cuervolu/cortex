package com.cortex.backend.education.mentorship.api.dto;

import com.cortex.backend.user.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object for a mentor")
public record MentorResponse(
    @Schema(description = "Unique identifier of the mentor request", example = "1")
    Long id,

    @Schema(description = "Area of expertise of the mentor", example = "Java Development")
    String area,

    @Schema(description = "User information of the mentor")
    UserResponse userResponse
) {}