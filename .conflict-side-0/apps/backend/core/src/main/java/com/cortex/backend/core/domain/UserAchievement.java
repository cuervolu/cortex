package com.cortex.backend.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievement", indexes = {
    @Index(name = "idx_user_achievement_user_id", columnList = "user_id"),
    @Index(name = "idx_user_achievement_achievement_id", columnList = "achievement_id"),
    @Index(name = "idx_user_achievement_obtained_date", columnList = "obtained_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {
  @EmbeddedId
  private UserAchievementKey id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("achievementId")
  @JoinColumn(name = "achievement_id")
  private Achievement achievement;

  @Column(name = "obtained_date", nullable = false)
  private LocalDateTime obtainedDate;
}