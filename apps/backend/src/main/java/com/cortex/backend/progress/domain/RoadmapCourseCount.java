package com.cortex.backend.progress.domain;

import com.cortex.backend.education.roadmap.Roadmap;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roadmap_course_count")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapCourseCount {

  @Id
  @Column(name = "roadmap_id")
  private Long roadmapId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "roadmap_id")
  private Roadmap roadmap;

  @Column(name = "total_courses", nullable = false)
  private Integer totalCourses;
}