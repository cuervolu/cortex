package com.cortex.backend.education.roadmap.api.dto;

import com.cortex.backend.core.domain.EnrollmentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record RoadmapEnrollmentResponse(
    @JsonProperty("user_id")
    Long userId,
    @JsonProperty("roadmap_id")
    Long roadmapId,

    @JsonProperty("enrollment_date")
    LocalDateTime enrollmentDate,

    EnrollmentStatus status,

    @JsonProperty("last_activity_date")
    LocalDateTime lastActivityDate,
    double progress
) {

}