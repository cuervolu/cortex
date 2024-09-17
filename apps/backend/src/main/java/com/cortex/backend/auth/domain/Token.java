package com.cortex.backend.auth.domain;

import com.cortex.backend.user.domain.TokenType;
import com.cortex.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "token", indexes = {
    @Index(name = "idx_token_token", columnList = "token"),
    @Index(name = "idx_token_user_id", columnList = "user_id"),
    @Index(name = "idx_token_expires_at", columnList = "expires_at")
})
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
