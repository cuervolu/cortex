package com.cortex.backend.progress.domain;

import com.cortex.backend.education.roadmap.domain.Roadmap;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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