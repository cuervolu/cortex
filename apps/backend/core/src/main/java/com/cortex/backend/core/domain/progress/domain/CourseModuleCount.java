package com.cortex.backend.core.domain.progress.domain;

import com.cortex.backend.core.domain.Course;
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
@Table(name = "course_module_count")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseModuleCount {

  @Id
  @Column(name = "course_id")
  private Long courseId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "course_id")
  private Course course;

  @Column(name = "total_modules", nullable = false)
  private Integer totalModules;
}