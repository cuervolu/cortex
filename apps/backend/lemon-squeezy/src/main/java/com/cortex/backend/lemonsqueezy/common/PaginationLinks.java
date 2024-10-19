package com.cortex.backend.lemonsqueezy.common;

import lombok.Data;

@Data
public class PaginationLinks {
  private String first;
  private String last;
  private String next;
  private String prev;
}
