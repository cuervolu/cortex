package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapEnrollmentId implements Serializable {
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "roadmap_id")
  private Long roadmapId;
}