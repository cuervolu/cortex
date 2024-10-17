package com.cortex.backend.education.mentorship.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "Feedback for a mentorship")
public record MentorshipFeedback(
    @Schema(description = "Rating of the mentorship (1-5)", example = "4")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    Integer rating,

    @Schema(description = "Comments about the mentorship")
    String comments
) {
  public MentorshipFeedback {
    // Custom constructor to allow null values
    if (rating != null && (rating < 1 || rating > 5)) {
      throw new IllegalArgumentException("Rating must be between 1 and 5");
    }
  }
}