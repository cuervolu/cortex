package com.cortex.backend.lemonsqueezy.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationRequest {
  @JsonProperty("page[number]")
  private Integer pageNumber;
  @JsonProperty("page[size]")
  private Integer pageSize;
}