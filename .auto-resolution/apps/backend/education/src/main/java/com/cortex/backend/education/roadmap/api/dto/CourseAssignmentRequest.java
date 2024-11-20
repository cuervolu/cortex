package com.cortex.backend.education.roadmap.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CourseAssignmentRequest(
    @NotEmpty(message = "At least one course assignment is required")
    @Size(max = 100, message = "Cannot assign more than 100 courses at once")
    @Valid
    List<CourseAssignment> assignments
) {}
