package com.cortex.backend.entities.payments;

import com.cortex.backend.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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