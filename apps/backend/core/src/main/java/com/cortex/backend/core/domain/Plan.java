package com.cortex.backend.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "variant_id", nullable = false, unique = true)
  private Long variantId;

  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private String price;

  @Column(name = "is_usage_based")
  private boolean isUsageBased;

  private String interval;

  @Column(name = "interval_count")
  private Integer intervalCount;

  @Column(name = "trial_interval")
  private String trialInterval;

  @Column(name = "trial_interval_count")
  private Integer trialIntervalCount;

  private Integer sort;
}