package com.cortex.backend.lemonsqueezy.orders.items;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling Lemon Squeezy Order Item operations.
 * Extends PaginatedService to inherit common pagination functionality.
 */
public class OrderItemService extends PaginatedService<OrderItem> {

  private static final String ORDER_ITEMS_ENDPOINT = "order-items";

  // Filter constants
  private static final String FILTER_ORDER_ID = "filter[order_id]";
  private static final String FILTER_PRODUCT_ID = "filter[product_id]";
  private static final String FILTER_VARIANT_ID = "filter[variant_id]";

  // JSON field constants
  private static final String DATA = "data";
  private static final String ATTRIBUTES = "attributes";
  private static final String ID = "id";

  /**
   * Constructs a new OrderItemService with the given LemonSqueezyClient.
   *
   * @param client The LemonSqueezyClient to use for API requests
   */
  public OrderItemService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Retrieves an order item by its ID.
   *
   * @param orderItemId The ID of the order item to retrieve
   * @return The OrderItem object
   * @throws IOException If there's an error making the API request or parsing the response
   */
  public OrderItem getOrderItem(String orderItemId) throws IOException {
    String response = client.get(ORDER_ITEMS_ENDPOINT + "/" + orderItemId);
    return parseOrderItemResponse(response);
  }

  /**
   * Lists all order items with optional filtering and pagination.
   *
   * @param filter            The filter to apply to the list of order items
   * @param paginationRequest The pagination parameters
   * @return A paginated response containing the list of order items
   * @throws IOException If there's an error making the API request or parsing the response
   */
  public PaginatedResponse<OrderItem> listOrderItems(OrderItemFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(ORDER_ITEMS_ENDPOINT + queryParams, paginationRequest);
  }

  private String buildQueryParams(OrderItemFilter filter) {
    if (filter == null) {
      return "";
    }

    List<String> params = new ArrayList<>();
    if (filter.getOrderId() != null) {
      params.add(FILTER_ORDER_ID + "=" + filter.getOrderId());
    }
    if (filter.getProductId() != null) {
      params.add(FILTER_PRODUCT_ID + "=" + filter.getProductId());
    }
    if (filter.getVariantId() != null) {
      params.add(FILTER_VARIANT_ID + "=" + filter.getVariantId());
    }

    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private OrderItem parseOrderItemResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get(DATA);
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected OrderItem parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has(ATTRIBUTES)) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }
    JsonNode attributesNode = itemNode.get(ATTRIBUTES);
    OrderItem orderItem = objectMapper.treeToValue(attributesNode, OrderItem.class);
    orderItem.setId(itemNode.get(ID).asText());
    return orderItem;
  }
}