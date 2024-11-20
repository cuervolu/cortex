package com.cortex.backend.chat.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChatMessageRequest(
    @JsonProperty("mentorship_id")
    @NotNull(message = "Mentorship ID is required")
    @Positive(message = "Mentorship ID must be a positive number")
    Long mentorshipId,

    @JsonProperty("sender_id")
    @NotNull(message = "Sender ID is required")
    @Positive(message = "Sender ID must be a positive number")
    Long senderId,

    @JsonProperty("recipient_id")
    @NotNull(message = "Recipient ID is required")
    @Positive(message = "Recipient ID must be a positive number")
    Long recipientId,

    @JsonProperty("content")
    @NotBlank(message = "Content cannot be blank")
    String content
) {}