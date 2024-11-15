package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roadmap_enrollment", indexes = {
    @Index(name = "idx_enrollment_user_id", columnList = "user_id"),
    @Index(name = "idx_enrollment_roadmap_id", columnList = "roadmap_id"),
    @Index(name = "idx_enrollment_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapEnrollment {
  @EmbeddedId
  private RoadmapEnrollmentId id;

  @Column(nullable = false)
  private LocalDateTime enrollmentDate;

  @Enumerated(EnumType.STRING)
  private EnrollmentStatus status; //

  @Column(name = "last_activity_date")
  private LocalDateTime lastActivityDate;
}