package com.cortex.backend.lemonsqueezy.variants;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VariantService extends PaginatedService<Variant> {

  private static final String VARIANTS_ENDPOINT = "variants";

  public VariantService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Retrieves a single variant by its ID.
   *
   * @param variantId The ID of the variant to retrieve
   * @return The Variant object
   * @throws IOException If there's an error in the API call
   */
  public Variant getVariant(String variantId) throws IOException {
    String response = client.get(VARIANTS_ENDPOINT + "/" + variantId);
    return parseVariantResponse(response);
  }

  /**
   * Lists all variants with optional filtering and pagination.
   *
   * @param filter            The filter to apply
   * @param paginationRequest The pagination parameters
   * @return A paginated response of Variant objects
   * @throws IOException If there's an error in the API call
   */
  public PaginatedResponse<Variant> listVariants(VariantFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(VARIANTS_ENDPOINT + queryParams, paginationRequest);
  }

  private String buildQueryParams(VariantFilter filter) {
    if (filter == null) {
      return "";
    }

    List<String> params = new ArrayList<>();

    if (filter.getProductId() != null) {
      params.add("filter[product_id]=" + filter.getProductId());
    }

    if (filter.getStatus() != null) {
      params.add("filter[status]=" + filter.getStatus());
    }

    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private Variant parseVariantResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected Variant parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }

    String id = itemNode.get("id").asText();
    JsonNode attributesNode = itemNode.get("attributes");
    Variant variant = objectMapper.treeToValue(attributesNode, Variant.class);
    variant.setId(id);
    return variant;
  }
}