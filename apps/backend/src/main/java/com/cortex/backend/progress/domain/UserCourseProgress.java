package com.cortex.backend.progress.domain;

import com.cortex.backend.education.course.domain.Course;
import com.cortex.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_course_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCourseProgress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @Column(name = "modules_completed", nullable = false)
  private Integer modulesCompleted;

  @Column(name = "last_updated", nullable = false)
  private LocalDate lastUpdated;
}