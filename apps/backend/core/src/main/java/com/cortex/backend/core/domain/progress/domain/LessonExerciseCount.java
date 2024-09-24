package com.cortex.backend.core.domain.progress.domain;

import com.cortex.backend.core.domain.Lesson;
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