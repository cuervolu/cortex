package com.cortex.backend.engine.domain;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.education.lesson.Lesson;
import com.cortex.backend.education.solution.Solution;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "exercise")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_id", nullable = false)
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

  @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Solution> solutions;
}