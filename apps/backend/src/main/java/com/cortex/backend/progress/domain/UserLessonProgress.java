package com.cortex.backend.progress.domain;

import com.cortex.backend.education.lesson.domain.Lesson;
import com.cortex.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_lesson_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLessonProgress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "lesson_id", nullable = false)
  private Lesson lesson;

  @Column(name = "exercises_completed", nullable = false)
  private Integer exercisesCompleted;

  @Column(name = "last_updated", nullable = false)
  private LocalDate lastUpdated;
}