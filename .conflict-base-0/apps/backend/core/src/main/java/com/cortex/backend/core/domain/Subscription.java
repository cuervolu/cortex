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
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions",
    indexes = @Index(name = "subscriptions_index_0", columnList = "user_id, plan_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id", nullable = false)
  private SubscriptionPlan plan;

  @Column(nullable = false)
  private String status;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "next_billing_date")
  private LocalDate nextBillingDate;

  @Column(name = "mercadopago_subscription_id")
  private String mercadopagoSubscriptionId;

  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt;

  @Column(name = "updated_at")
  private LocalDate updatedAt;
}