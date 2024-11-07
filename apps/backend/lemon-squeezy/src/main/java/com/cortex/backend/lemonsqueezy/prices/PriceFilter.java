package com.cortex.backend.lemonsqueezy.prices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Filter parameters for listing prices.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceFilter {

  /**
   * Return only prices associated with the variant with this ID.
   */
  @JsonProperty("variant_id")
  private Long variantId;
}