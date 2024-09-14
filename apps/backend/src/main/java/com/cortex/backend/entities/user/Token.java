package com.cortex.backend.entities.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

  @Id
  @GeneratedValue
  private Long id;

  private String token;

  @Column(name = "token_type")
  @Enumerated(EnumType.STRING)
  private TokenType tokenType;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "validated_at")
  private LocalDateTime validatedAt;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;


  public boolean isExpired() {
    return LocalDateTime.now().isAfter(this.expiresAt);
  }
}
