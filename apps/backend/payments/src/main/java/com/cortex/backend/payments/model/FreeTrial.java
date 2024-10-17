package com.cortex.backend.payments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FreeTrial {

  private Integer frequency;

  @JsonProperty("frequency_type")
  private String frequencyType;
}
