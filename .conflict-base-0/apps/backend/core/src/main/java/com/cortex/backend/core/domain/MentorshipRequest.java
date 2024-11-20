package com.cortex.backend.core.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "mentorship_requests")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipRequest extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MentorshipRequestStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MentorshipRequestType type;

  @Column()
  private String area;

  @Column(columnDefinition = "TEXT")
  private String reason;
}