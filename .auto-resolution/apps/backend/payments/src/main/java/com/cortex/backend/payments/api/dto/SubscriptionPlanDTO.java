package com.cortex.backend.payments.api.dto;

import com.cortex.backend.core.domain.IntervalUnit;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubscriptionPlanDTO {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  
  @JsonProperty("interval_unit")
  private IntervalUnit intervalUnit;
  
  @JsonProperty("interval_count")
  private Integer intervalCount;
  
  @JsonProperty("currency_id")
  private String currencyId;
}