package com.cortex.backend.education.roadmap.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CourseAssignment(
    @JsonProperty("course_id")
    @NotNull(message = "Course ID is required")
    @Min(value = 1, message = "Course ID must be positive")
    Long courseId,

    @JsonProperty("display_order")
    @NotNull(message = "Display order is required")
    @Min(value = 0, message = "Display order must be non-negative")
    Integer displayOrder
) {

}