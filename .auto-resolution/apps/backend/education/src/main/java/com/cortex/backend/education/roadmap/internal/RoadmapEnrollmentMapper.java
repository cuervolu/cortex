package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.core.domain.RoadmapEnrollment;
import com.cortex.backend.education.roadmap.api.dto.RoadmapEnrollmentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoadmapEnrollmentMapper {
  default RoadmapEnrollmentResponse toResponse(
      RoadmapEnrollment enrollment,
      double progress
  ) {
    return new RoadmapEnrollmentResponse(
        enrollment.getId().getUserId(),
        enrollment.getId().getRoadmapId(),
        enrollment.getEnrollmentDate(),
        enrollment.getStatus(),
        enrollment.getLastActivityDate(),
        progress
    );
  }
}