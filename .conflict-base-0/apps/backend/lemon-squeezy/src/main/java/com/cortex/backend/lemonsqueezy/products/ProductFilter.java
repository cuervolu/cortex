package com.cortex.backend.lemonsqueezy.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
  @JsonProperty("store_id")
  private Long storeId;
  private String status;
}