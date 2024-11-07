package com.cortex.backend.lemonsqueezy.common;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponse<T> {
  private List<T> data;
  private PaginationMeta meta;
  private PaginationLinks links;
}
