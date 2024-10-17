package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions",
    indexes = @Index(name = "transactions_index_0", columnList = "user_id, status"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "lemon_squeezy_transaction_id", nullable = false)
  private String lemonSqueezyTransactionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private String status;

  @Column(name = "payment_method", nullable = false)
  private String paymentMethod;

  @Column(name = "transaction_date", nullable = false)
  private OffsetDateTime transactionDate;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt;

  @Column(name = "updated_at")
  private LocalDate updatedAt;
}