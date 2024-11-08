package com.cortex.backend.core.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "exercise", indexes = {
    @Index(name = "idx_exercise_title", columnList = "title"),
    @Index(name = "idx_exercise_slug", columnList = "slug"),
    @Index(name = "idx_exercise_lesson", columnList = "lesson_id"),
    @Index(name = "idx_exercise_lesson_order", columnList = "lesson_id,display_order"),
    @Index(name = "idx_exercise_points", columnList = "points"),
    @Index(name = "idx_exercise_last_sync", columnList = "last_github_sync"),
    @Index(name = "idx_exercise_created_at", columnList = "created_at"),
    @Index(name = "idx_exercise_status", columnList = "status"),
    @Index(name = "idx_exercise_pending_lesson", columnList = "pending_lesson_slug"),
    @Index(name = "idx_exercise_pending_creator", columnList = "pending_creator")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_id")
  private Lesson lesson;

  @Column(nullable = false)
  private String slug;

  @Column(nullable = false)
  private String title;

  @Column(name = "github_path", nullable = false)
  private String githubPath;

  @Column(name = "last_github_sync", nullable = false)
  private LocalDateTime lastGithubSync;

  @Column(nullable = false)
  private Integer points;

  @Column(columnDefinition = "TEXT")
  private String instructions;

  @Column(columnDefinition = "TEXT")
  private String hints;

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Solution> solutions;

  @Column(name = "display_order", nullable = false)
  private Integer displayOrder = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private ExerciseStatus status = ExerciseStatus.DRAFT;

  @ElementCollection
  @CollectionTable(
      name = "exercise_prerequisites",
      joinColumns = @JoinColumn(name = "exercise_id"),
      indexes = @Index(name = "idx_prerequisite_exercise", columnList = "exercise_id,prerequisiteExercises")
  )
  private Set<Long> prerequisiteExercises = new HashSet<>();

  @ElementCollection
  @CollectionTable(
      name = "exercise_tags",
      joinColumns = @JoinColumn(name = "exercise_id"),
      indexes = @Index(name = "idx_exercise_tag", columnList = "exercise_id,tags")
  )
  private Set<String> tags = new HashSet<>();

  @Column(name = "pending_lesson_slug")
  private String pendingLessonSlug;

  @Column(name = "pending_creator")
  private String pendingCreator;

  @Column(name = "difficulty")
  @Enumerated(EnumType.STRING)
  private ExerciseDifficulty difficulty = ExerciseDifficulty.BEGINNER;

  @Column(name = "estimated_time_minutes")
  private Integer estimatedTimeMinutes;

  @Column(name = "review_notes", columnDefinition = "TEXT")
  private String reviewNotes;


}