package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementKey implements Serializable {
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "achievement_id")
  private Long achievementId;
}