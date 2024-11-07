package com.cortex.backend.lemonsqueezy.prices;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 * Service for interacting with the Lemon Squeezy Prices API.
 */
public class PriceService extends PaginatedService<Price> {

  private static final String PRICES_ENDPOINT = "prices";

  public PriceService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Retrieves a single price by its ID.
   *
   * @param priceId The ID of the price to retrieve
   * @return The Price object
   * @throws IOException If there's an error in the API call
   */
  public Price getPrice(String priceId) throws IOException {
    String response = client.get(PRICES_ENDPOINT + "/" + priceId);
    return parsePriceResponse(response);
  }

  /**
   * Lists all prices with optional filtering and pagination.
   * Results are ordered by created_at in descending order.
   *
   * @param filter The filter to apply
   * @param paginationRequest The pagination parameters
   * @return A paginated response of Price objects
   * @throws IOException If there's an error in the API call
   */
  public PaginatedResponse<Price> listPrices(PriceFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(PRICES_ENDPOINT + queryParams, paginationRequest);
  }

  /**
   * Lists all prices for a specific variant.
   * This is a convenience method that wraps listPrices with a variantId filter.
   *
   * @param variantId The ID of the variant to get prices for
   * @return A paginated response of Price objects
   * @throws IOException If there's an error in the API call
   */
  public PaginatedResponse<Price> listPricesForVariant(Long variantId) throws IOException {
    return listPrices(
        PriceFilter.builder().variantId(variantId).build(),
        null
    );
  }

  private String buildQueryParams(PriceFilter filter) {
    if (filter == null || filter.getVariantId() == null) {
      return "";
    }

    return "?filter[variant_id]=" + filter.getVariantId();
  }

  private Price parsePriceResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected Price parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }

    String id = itemNode.get("id").asText();
    JsonNode attributesNode = itemNode.get("attributes");
    Price price = objectMapper.treeToValue(attributesNode, Price.class);
    price.setId(id);
    return price;
  }
}