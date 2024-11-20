package com.cortex.backend.core.common;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import java.util.ArrayList;
import java.util.List;

public class SortUtils {
  private static final String SORT_DELIMITER = ",";
  private static final String DIRECTION_DELIMITER = ":";

  public static Sort parseSort(String[] sort) {
    if (sort == null || sort.length == 0) {
      return Sort.by(Direction.DESC, "createdAt"); // Default sorting
    }

    List<Order> orders = new ArrayList<>();

    for (String sortParam : sort) {
      String[] parts = sortParam.split(DIRECTION_DELIMITER);
      String property = parts[0];

      Direction direction = Direction.ASC;
      if (parts.length > 1) {
        direction = Direction.fromString(parts[1]);
      }

      orders.add(new Order(direction, property));
    }

    return Sort.by(orders);
  }

  public static boolean isValidSortProperty(String property, Class<?> entityClass) {
    try {
      return entityClass.getDeclaredField(property) != null;
    } catch (NoSuchFieldException e) {
      return false;
    }
  }
}