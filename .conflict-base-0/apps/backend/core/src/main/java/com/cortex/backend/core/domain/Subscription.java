package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "lemon_squeezy_id", nullable = false, unique = true)
  private String lemonSqueezyId;

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String status;

  @Column(name = "status_formatted", nullable = false)
  private String statusFormatted;

  @Column(name = "renews_at")
  private String renewsAt;

  @Column(name = "ends_at")
  private String endsAt;

  @Column(name = "trial_ends_at")
  private String trialEndsAt;

  @Column(nullable = false)
  private String price;

  @Column(name = "is_usage_based")
  private boolean isUsageBased;

  @Column(name = "is_paused")
  private boolean isPaused;

  @Column(name = "subscription_item_id")
  private Long subscriptionItemId;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;
}