package com.cortex.backend.lemonsqueezy.checkouts;

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

public class CheckoutService extends PaginatedService<Checkout> {

  private static final String TYPE = "type";
  private static final String DATA = "data";
  private static final String ATTRIBUTES = "attributes";
  private static final String RELATIONSHIPS = "relationships";
  private static final String STORE = "store";
  private static final String VARIANT = "variant";
  private static final String CHECKOUTS = "checkouts";
  private static final String STORES = "stores";
  private static final String VARIANTS = "variants";
  private static final String FILTER_STORE_ID = "filter[store_id]";
  private static final String FILTER_VARIANT_ID = "filter[variant_id]";

  public CheckoutService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  public Checkout createCheckout(CheckoutRequest request) throws IOException {
    Map<String, Object> data = new HashMap<>();
    data.put(TYPE, CHECKOUTS);
    data.put(ATTRIBUTES, objectMapper.convertValue(request, Map.class));

    Map<String, Object> relationships = new HashMap<>();
    relationships.put(STORE, createRelationship(STORES, String.valueOf(request.getStoreId())));
    relationships.put(VARIANT, createRelationship(VARIANTS, String.valueOf(request.getVariantId())));
    data.put(RELATIONSHIPS, relationships);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put(DATA, data);

    String response = client.post(CHECKOUTS, requestBody);
    return parseCheckoutResponse(response);
  }

  public Checkout getCheckout(String checkoutId) throws IOException {
    String response = client.get(CHECKOUTS + "/" + checkoutId);
    return parseCheckoutResponse(response);
  }

  public PaginatedResponse<Checkout> listCheckouts(Long storeId, Long variantId, PaginationRequest paginationRequest) throws IOException {
    StringBuilder endpoint = new StringBuilder(CHECKOUTS);
    List<String> params = new ArrayList<>();

    if (storeId != null) {
      params.add(FILTER_STORE_ID + "=" + storeId);
    }
    if (variantId != null) {
      params.add(FILTER_VARIANT_ID + "=" + variantId);
    }

    if (!params.isEmpty()) {
      endpoint.append("?").append(String.join("&", params));
    }

    return getPaginatedResponse(endpoint.toString(), paginationRequest);
  }

  private Checkout parseCheckoutResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get(DATA);
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected Checkout parseItem(JsonNode itemNode) throws IOException {
    JsonNode attributesNode = itemNode.get(ATTRIBUTES);
    if (attributesNode == null || !attributesNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'attributes' field");
    }

    Checkout checkout = objectMapper.treeToValue(attributesNode, Checkout.class);
    checkout.setId(itemNode.get("id").asText());
    return checkout;
  }

  private Map<String, Object> createRelationship(String type, String id) {
    Map<String, Object> relationship = new HashMap<>();
    relationship.put(DATA, Map.of(TYPE, type, "id", id));
    return relationship;
  }
}