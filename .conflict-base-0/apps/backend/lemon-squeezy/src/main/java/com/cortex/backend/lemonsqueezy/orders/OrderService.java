package com.cortex.backend.lemonsqueezy.orders;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for handling Lemon Squeezy Order operations. Extends PaginatedService to inherit
 * common pagination functionality.
 */
public class OrderService extends PaginatedService<Order> {

  private static final String ORDERS_ENDPOINT = "orders";
  private static final String GENERATE_INVOICE_ENDPOINT = "/generate-invoice";
  private static final String REFUND_ENDPOINT = "/refund";

  // JSON field constants
  private static final String DATA = "data";
  private static final String TYPE = "type";
  private static final String ATTRIBUTES = "attributes";
  private static final String ID = "id";
  private static final String META = "meta";
  private static final String URLS = "urls";
  private static final String DOWNLOAD_INVOICE = "download_invoice";

  // Filter constants
  private static final String FILTER_STORE_ID = "filter[store_id]";
  private static final String FILTER_USER_EMAIL = "filter[user_email]";
  private static final String FILTER_STATUS = "filter[status]";
  private static final String FILTER_CUSTOMER_ID = "filter[customer_id]";

  /**
   * Constructs a new OrderService with the given LemonSqueezyClient.
   *
   * @param client The LemonSqueezyClient to use for API requests
   */
  public OrderService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Retrieves an order by its ID.
   *
   * @param orderId The ID of the order to retrieve
   * @return The Order object
   * @throws IOException If there's an error making the API request or parsing the response
   */
  public Order getOrder(String orderId) throws IOException {
    String response = client.get(ORDERS_ENDPOINT + "/" + orderId);
    return parseOrderResponse(response);
  }

  /**
   * Lists all orders with optional filtering and pagination.
   *
   * @param filter            The filter to apply to the list of orders
   * @param paginationRequest The pagination parameters
   * @return A paginated response containing the list of orders
   * @throws IOException If there's an error making the API request or parsing the response
   */
  public PaginatedResponse<Order> listOrders(OrderFilter filter,
      PaginationRequest paginationRequest)
      throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(ORDERS_ENDPOINT + queryParams, paginationRequest);
  }

  /**
   * Generates an invoice for a specific order.
   *
   * @param orderId The ID of the order to generate an invoice for
   * @param request The invoice generation request parameters
   * @return The URL to download the generated invoice
   * @throws IOException If there's an error making the API request or parsing the response
   */
  public String generateInvoice(String orderId, InvoiceGenerationRequest request)
      throws IOException {
    String endpoint = ORDERS_ENDPOINT + "/" + orderId + GENERATE_INVOICE_ENDPOINT;

    Map<String, String> queryParams = new HashMap<>();
    if (request.getName() != null) {
      queryParams.put("name", request.getName());
    }
    if (request.getAddress() != null) {
      queryParams.put("address", request.getAddress());
    }
    if (request.getCity() != null) {
      queryParams.put("city", request.getCity());
    }
    if (request.getState() != null) {
      queryParams.put("state", request.getState());
    }
    if (request.getZipCode() != null) {
      queryParams.put("zip_code", request.getZipCode());
    }
    if (request.getCountry() != null) {
      queryParams.put("country", request.getCountry());
    }
    if (request.getNotes() != null) {
      queryParams.put("notes", request.getNotes());
    }

    String response = client.post(endpoint, queryParams);
    return parseInvoiceGenerationResponse(response);
  }

  /**
   * Issues a refund for a specific order.
   *
   * @param orderId The ID of the order to refund
   * @param request The refund request containing the amount to refund (optional, null for full
   *                refund)
   * @return The updated Order object after the refund
   * @throws IOException If there's an error making the API request or parsing the response
   */
  public Order issueRefund(String orderId, RefundRequest request) throws IOException {
    String endpoint = ORDERS_ENDPOINT + "/" + orderId + REFUND_ENDPOINT;

    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put(TYPE, ORDERS_ENDPOINT);
    data.put(ID, orderId);

    if (request != null && request.getAmount() != null) {
      Map<String, Object> attributes = new HashMap<>();
      attributes.put("amount", request.getAmount());
      data.put(ATTRIBUTES, attributes);
    }

    requestBody.put(DATA, data);

    String response = client.post(endpoint, requestBody);
    return parseOrderResponse(response);
  }

  private String parseInvoiceGenerationResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode metaNode = rootNode.get(META);
    if (metaNode != null && metaNode.has(URLS)) {
      JsonNode urlsNode = metaNode.get(URLS);
      if (urlsNode.has(DOWNLOAD_INVOICE)) {
        return urlsNode.get(DOWNLOAD_INVOICE).asText();
      }
    }
    throw new IOException("Invalid response format: missing download_invoice URL");
  }

  private String buildQueryParams(OrderFilter filter) {
    if (filter == null) {
      return "";
    }

    List<String> params = new ArrayList<>();
    if (filter.getStoreId() != null) {
      params.add(FILTER_STORE_ID + "=" + filter.getStoreId());
    }
    if (filter.getUserEmail() != null) {
      params.add(FILTER_USER_EMAIL + "=" + filter.getUserEmail());
    }
    if (filter.getStatus() != null) {
      params.add(FILTER_STATUS + "=" + filter.getStatus());
    }
    if (filter.getCustomerId() != null) {
      params.add(FILTER_CUSTOMER_ID + "=" + filter.getCustomerId());
    }

    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private Order parseOrderResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get(DATA);
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected Order parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has(ATTRIBUTES)) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }
    JsonNode attributesNode = itemNode.get(ATTRIBUTES);
    Order order = objectMapper.treeToValue(attributesNode, Order.class);
    order.setId(itemNode.get(ID).asText());
    return order;
  }
}