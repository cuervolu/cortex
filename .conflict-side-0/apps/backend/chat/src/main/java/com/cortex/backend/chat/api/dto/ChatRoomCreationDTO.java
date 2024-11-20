package com.cortex.backend.chat.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for creating a chat room")
public record ChatRoomCreationDTO(
    @Schema(description = "ID of the mentorship", example = "1")
    @JsonProperty("mentorship_id")
    Long mentorshipId,

    @Schema(description = "ID of the mentor", example = "100")
    @JsonProperty("mentor_id")
    Long mentorId,

    @Schema(description = "ID of the mentee", example = "200")
    @JsonProperty("mentee_id")
    Long menteeId
) {

}