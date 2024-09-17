package com.cortex.backend.progress.domain;

import com.cortex.backend.education.course.Course;
import jakarta.persistence.*;
import lombok.*;

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