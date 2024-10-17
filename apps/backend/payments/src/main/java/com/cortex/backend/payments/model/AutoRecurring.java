package com.cortex.backend.payments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class AutoRecurring {
  private Integer frequency;
  
  @JsonProperty("frequency_type")
  private String frequencyType;
  
  private Integer repetitions;
  
  @JsonProperty("billing_day")
  private Integer billingDay;
  
  @JsonProperty("billing_day_proportional")
  private Boolean billingDayProportional;
  
  @JsonProperty("free_trial")
  private FreeTrial freeTrial;
  
  @JsonProperty("transaction_amount")
  private BigDecimal transactionAmount;
  
  @JsonProperty("currency_id")
  private String currencyId;
}
