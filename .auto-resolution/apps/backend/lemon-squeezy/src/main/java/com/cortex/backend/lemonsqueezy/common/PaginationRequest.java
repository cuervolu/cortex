package com.cortex.backend.lemonsqueezy.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaginationRequest {
  @JsonProperty("page[number]")
  private Integer pageNumber;
  @JsonProperty("page[size]")
  private Integer pageSize;
}