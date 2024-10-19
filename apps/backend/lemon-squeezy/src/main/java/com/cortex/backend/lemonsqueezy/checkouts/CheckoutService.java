package com.cortex.backend.lemonsqueezy.checkouts;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutService {

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

  private final LemonSqueezyClient client;
  private final ObjectMapper objectMapper;

  public CheckoutService(LemonSqueezyClient client) {
    this.client = client;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  public Checkout createCheckout(CheckoutRequest request) throws IOException {
    Map<String, Object> data = new HashMap<>();
    data.put(TYPE, CHECKOUTS);
    data.put(ATTRIBUTES, objectMapper.convertValue(request, Map.class));

    Map<String, Object> relationships = new HashMap<>();
    relationships.put(STORE, createRelationship(STORES, String.valueOf(request.getStoreId())));
    relationships.put(VARIANT,
        createRelationship(VARIANTS, String.valueOf(request.getVariantId())));
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

  public List<Checkout> listCheckouts(Long storeId, Long variantId) throws IOException {
    StringBuilder endpoint = new StringBuilder(CHECKOUTS);
    if (storeId != null || variantId != null) {
      endpoint.append("?");
      if (storeId != null) {
        endpoint.append(FILTER_STORE_ID).append("=").append(storeId);
      }
      if (variantId != null) {
        if (storeId != null) {
          endpoint.append("&");
        }
        endpoint.append(FILTER_VARIANT_ID).append("=").append(variantId);
      }
    }
    String response = client.get(endpoint.toString());
    return parseCheckoutListResponse(response);
  }

  private Checkout parseCheckoutResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get(DATA);
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    JsonNode attributesNode = dataNode.get(ATTRIBUTES);
    if (attributesNode == null || !attributesNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'attributes' field");
    }

    return objectMapper.treeToValue(attributesNode, Checkout.class);
  }

  private List<Checkout> parseCheckoutListResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get(DATA);
    if (dataNode == null || !dataNode.isArray()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    List<Checkout> checkouts = new ArrayList<>();
    for (JsonNode item : dataNode) {
      JsonNode attributesNode = item.get(ATTRIBUTES);
      if (attributesNode != null && attributesNode.isObject()) {
        checkouts.add(objectMapper.treeToValue(attributesNode, Checkout.class));
      }
    }

    return checkouts;
  }

  private Map<String, Object> createRelationship(String type, String id) {
    Map<String, Object> relationship = new HashMap<>();
    relationship.put(DATA, Map.of(TYPE, type, "id", id));
    return relationship;
  }
}