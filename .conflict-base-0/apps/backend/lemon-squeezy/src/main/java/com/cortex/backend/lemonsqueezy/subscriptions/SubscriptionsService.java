package com.cortex.backend.lemonsqueezy.subscriptions;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriptionsService extends PaginatedService<Subscription> {

  private static final String SUBSCRIPTIONS_ENDPOINT = "subscriptions";

  public SubscriptionsService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  public Subscription getSubscription(String subscriptionId) throws IOException {
    String response = client.get(SUBSCRIPTIONS_ENDPOINT + "/" + subscriptionId);
    return parseSubscriptionResponse(response);
  }

  public PaginatedResponse<Subscription> listSubscriptions(SubscriptionFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(SUBSCRIPTIONS_ENDPOINT + queryParams, paginationRequest);
  }

  public Subscription updateSubscription(String subscriptionId,
      SubscriptionUpdateRequest updateRequest) throws IOException {
    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put("type", SUBSCRIPTIONS_ENDPOINT);
    data.put("id", subscriptionId);
    data.put("attributes", objectMapper.convertValue(updateRequest, Map.class));
    requestBody.put("data", data);

    String response = client.patch(SUBSCRIPTIONS_ENDPOINT + "/" + subscriptionId, requestBody);
    return parseSubscriptionResponse(response);
  }

  public Subscription cancelSubscription(String subscriptionId) throws IOException {
    String response = client.delete(SUBSCRIPTIONS_ENDPOINT + "/" + subscriptionId);
    return parseSubscriptionResponse(response);
  }

  private String buildQueryParams(SubscriptionFilter filter) {
    if (filter == null) {
      return "";
    }

    List<String> params = new ArrayList<>();
    if (filter.getStoreId() != null) {
      params.add("filter[store_id]=" + filter.getStoreId());
    }
    if (filter.getOrderId() != null) {
      params.add("filter[order_id]=" + filter.getOrderId());
    }
    if (filter.getOrderItemId() != null) {
      params.add("filter[order_item_id]=" + filter.getOrderItemId());
    }
    if (filter.getProductId() != null) {
      params.add("filter[product_id]=" + filter.getProductId());
    }
    if (filter.getVariantId() != null) {
      params.add("filter[variant_id]=" + filter.getVariantId());
    }
    if (filter.getUserEmail() != null) {
      params.add("filter[user_email]=" + filter.getUserEmail());
    }
    if (filter.getStatus() != null) {
      params.add("filter[status]=" + filter.getStatus());
    }

    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private Subscription parseSubscriptionResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected Subscription parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }
    JsonNode attributesNode = itemNode.get("attributes");
    Subscription subscription = objectMapper.treeToValue(attributesNode, Subscription.class);
    subscription.setId(itemNode.get("id").asText());
    return subscription;
  }
}