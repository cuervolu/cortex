package com.cortex.backend.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievement_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "icon_url")
  private String iconUrl;
}