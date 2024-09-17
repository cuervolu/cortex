package com.cortex.backend.progress.domain;

import com.cortex.backend.education.lesson.Lesson;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_exercise_count")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonExerciseCount {

  @Id
  @Column(name = "lesson_id")
  private Long lessonId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "lesson_id")
  private Lesson lesson;

  @Column(name = "total_exercises", nullable = false)
  private Integer totalExercises;
}