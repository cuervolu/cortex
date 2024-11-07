package com.cortex.backend.lemonsqueezy.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Page {
  @JsonProperty("currentPage")
  private int currentPage;
  private int from;
  @JsonProperty("lastPage")
  private int lastPage;
  @JsonProperty("perPage")
  private int perPage;
  private int to;
  private int total;
}
