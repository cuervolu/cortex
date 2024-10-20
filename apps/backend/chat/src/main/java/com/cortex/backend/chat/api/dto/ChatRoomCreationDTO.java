package com.cortex.backend.chat.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for creating a chat room")
public record ChatRoomCreationDTO(
    @Schema(description = "ID of the mentorship", example = "1")
    Long mentorshipId,

    @Schema(description = "ID of the mentor", example = "100")
    Long mentorId,

    @Schema(description = "ID of the mentee", example = "200")
    Long menteeId
) {}