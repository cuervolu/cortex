package com.cortex.backend.education.mentorship.api.dto;

import com.cortex.backend.core.domain.MentorshipRequestStatus;
import com.cortex.backend.core.domain.MentorshipRequestType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Response DTO for Mentorship Request")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MentorshipRequestResponse(
    @Schema(description = "ID of the mentorship request")
    Long id,

    @Schema(description = "ID of the user who made the request")
    @JsonProperty("user_id")
    Long userId,

    @Schema(description = "Status of the mentorship request")
    MentorshipRequestStatus status,

    @Schema(description = "Type of the mentorship request")
    MentorshipRequestType type,

    @Schema(description = "Area of expertise (for mentor applications)")
    String area,

    @Schema(description = "Reason for the request")
    String reason,

    @Schema(description = "Creation date of the request")
    @JsonProperty("created_at")
    LocalDateTime createdAt,

    @Schema(description = "Last update date of the request")
    @JsonProperty("updated_at")
    LocalDateTime updatedAt) {
}