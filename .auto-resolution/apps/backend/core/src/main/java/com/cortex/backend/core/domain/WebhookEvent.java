package com.cortex.backend.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "event_name", nullable = false)
  private String eventName;

  private boolean processed;

  @Column(columnDefinition = "jsonb", nullable = false)
  private String body;

  @Column(name = "processing_error")
  private String processingError;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}