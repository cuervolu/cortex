package com.cortex.backend.education.mentorship.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for creating a new mentorship")
public record MentorshipCreation(
    @Schema(description = "ID of the mentee", example = "1")
        @JsonProperty(value = "mentee_id")
    Long menteeId
) {}