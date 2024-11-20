package com.cortex.backend.education.roadmap.api;

import com.cortex.backend.core.domain.EnrollmentStatus;
import com.cortex.backend.core.domain.RoadmapEnrollment;
import com.cortex.backend.core.domain.RoadmapEnrollmentId;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapEnrollmentRepository extends
    CrudRepository<RoadmapEnrollment, RoadmapEnrollmentId> {

  List<RoadmapEnrollment> findByIdUserId(Long userId);

  @Query("SELECT re FROM RoadmapEnrollment re " +
      "WHERE re.id.userId = :userId AND re.status = :status")
  List<RoadmapEnrollment> findActiveEnrollments(Long userId, EnrollmentStatus status);

  @Query("SELECT COUNT(re) > 0 FROM RoadmapEnrollment re " +
      "WHERE re.id.userId = :userId AND re.id.roadmapId = :roadmapId")
  boolean isEnrolled(Long userId, Long roadmapId);
}