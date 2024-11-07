package com.cortex.backend.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "user_progress", indexes = {
    @Index(name = "idx_user_progress_user_id", columnList = "user_id"),
    @Index(name = "idx_user_progress_entity_id", columnList = "entity_id"),
    @Index(name = "idx_user_progress_entity_type", columnList = "entity_type"),
    @Index(name = "idx_user_progress_status", columnList = "status"),
    @Index(name = "idx_user_progress_completion_date", columnList = "completion_date")
})
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserProgress {

  @EmbeddedId
  private UserProgressKey id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private ProgressStatus status;

  @Column(name = "completion_date")
  private LocalDateTime completionDate;
}