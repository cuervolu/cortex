package com.cortex.backend.payments.api.dto;

import com.cortex.backend.core.domain.IntervalUnit;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class CreateSubscriptionPlanDTO {

  @NotBlank(message = "Name is required")
  @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.01", message = "Price must be greater than 0")
  private BigDecimal price;

  @NotNull(message = "Interval unit is required")
  @JsonProperty("interval_unit")
  private IntervalUnit intervalUnit;

  @NotNull(message = "Interval count is required")
  @Positive(message = "Interval count must be positive")
  @JsonProperty("interval_count")
  private Integer intervalCount;

  @NotBlank(message = "Currency ID is required")
  @Size(min = 3, max = 3, message = "Currency ID must be 3 characters long")
  @JsonProperty("currency_id")
  private String currencyId;
}