package com.cortex.backend.education.solution;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.engine.domain.Exercise;
import com.cortex.backend.engine.domain.Submission;
import com.cortex.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "solution")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Solution extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_id", nullable = false)
  private Exercise exercise;

  @Column(nullable = false)
  private Long status;

  @Column(name = "points_earned", nullable = false)
  private Integer pointsEarned;

  @OneToMany(mappedBy = "solution", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Submission> submissions;
}