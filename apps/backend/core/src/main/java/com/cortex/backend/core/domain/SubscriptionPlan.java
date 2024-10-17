package com.cortex.backend.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(name = "interval_unit", nullable = false)
  private IntervalUnit intervalUnit;

  @Column(name = "interval_count", nullable = false)
  private Integer intervalCount;

  @Column(name = "mercado_pago_plan_id")
  private String mercadoPagoPlanId;

  @Column(name = "currency_id", nullable = false)
  private String currencyId;

  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt;

  @Column(name = "updated_at")
  private LocalDate updatedAt;
}