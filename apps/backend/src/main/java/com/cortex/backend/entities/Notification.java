package com.cortex.backend.entities;

import com.cortex.backend.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications",
    indexes = @Index(name = "notifications_index_0", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private String status;

  @Column(name = "read_at")
  private OffsetDateTime readAt;

  @PrePersist
  protected void onNotificationCreate() {
    if (status == null) {
      status = "unread";
    }
  }
}