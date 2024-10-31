package com.cortex.backend.lemonsqueezy.products;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class ProductService extends PaginatedService<Product> {

  private static final String PRODUCTS_ENDPOINT = "products";

  public ProductService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Retrieves a single product by its ID.
   *
   * @param productId The ID of the product to retrieve
   * @return The Product object
   * @throws IOException If there's an error in the API call
   */
  public Product getProduct(String productId) throws IOException {
    String response = client.get(PRODUCTS_ENDPOINT + "/" + productId);
    return parseProductResponse(response);
  }

  /**
   * Lists all products with optional filtering and pagination.
   *
   * @param filter The filter to apply
   * @param paginationRequest The pagination parameters
   * @return A paginated response of Product objects
   * @throws IOException If there's an error in the API call
   */
  public PaginatedResponse<Product> listProducts(ProductFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(PRODUCTS_ENDPOINT + queryParams, paginationRequest);
  }

  private String buildQueryParams(ProductFilter filter) {
    if (filter == null || filter.getStoreId() == null) {
      return "";
    }

    return "?filter[store_id]=" + filter.getStoreId();
  }

  private Product parseProductResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected Product parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }

    String id = itemNode.get("id").asText();
    JsonNode attributesNode = itemNode.get("attributes");
    Product product = objectMapper.treeToValue(attributesNode, Product.class);
    product.setId(id); 
    return product;
  }
}