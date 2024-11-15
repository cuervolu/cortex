package com.cortex.backend.education.roadmap.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoadmapEnrollmentRequest(
    @JsonProperty("roadmap_id")
    Long roadmapId

) {

}
