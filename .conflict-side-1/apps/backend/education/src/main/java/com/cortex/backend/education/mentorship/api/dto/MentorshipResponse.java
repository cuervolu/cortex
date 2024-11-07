package com.cortex.backend.education.mentorship.api.dto;

import com.cortex.backend.core.domain.MentorshipStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Data transfer object for Mentorship")
public record MentorshipResponse(
    @Schema(description = "ID of the mentorship", example = "1")
    Long id,

    @Schema(description = "ID of the mentor", example = "100")
    @JsonProperty("mentor_id")
    Long mentorId,

    @Schema(description = "ID of the mentee", example = "200")
    @JsonProperty("mentee_id")
    Long menteeId,

    @Schema(description = "Start date of the mentorship")
    @JsonProperty("start_date")
    LocalDate startDate,

    @Schema(description = "End date of the mentorship")
    @JsonProperty("end_date")
    LocalDate endDate,

    @Schema(description = "Status of the mentorship")
    MentorshipStatus status,

    @Schema(description = "Notes of the mentorship")
    String notes,

    @Schema(description = "Feedback rating for the mentorship", example = "5")
    @JsonProperty("feedback_rating")
    Integer feedbackRating,

    @Schema(description = "Feedback comments for the mentorship")
    @JsonProperty("feedback_comments")
    String feedbackComments
) {
}