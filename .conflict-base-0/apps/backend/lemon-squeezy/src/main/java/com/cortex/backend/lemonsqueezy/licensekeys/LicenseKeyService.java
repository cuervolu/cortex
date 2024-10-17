package com.cortex.backend.lemonsqueezy.licensekeys;

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

public class LicenseKeyService extends PaginatedService<LicenseKey> {

  private static final String LICENSE_KEYS_ENDPOINT = "license-keys";

  public LicenseKeyService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Retrieves the license key with the given ID.
   *
   * @param licenseKeyId The ID of the license key to retrieve.
   * @return A LicenseKey object.
   * @throws IOException If there's an error in the API call.
   */
  public LicenseKey getLicenseKey(String licenseKeyId) throws IOException {
    String response = client.get(LICENSE_KEYS_ENDPOINT + "/" + licenseKeyId);
    return parseLicenseKeyResponse(response);
  }

  /**
   * Updates a license key with the given ID.
   *
   * @param licenseKeyId  The ID of the license key to update.
   * @param updateRequest The update request containing the fields to update.
   * @return The updated LicenseKey object.
   * @throws IOException If there's an error in the API call.
   */
  public LicenseKey updateLicenseKey(String licenseKeyId, LicenseKeyUpdateRequest updateRequest)
      throws IOException {
    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put("type", LICENSE_KEYS_ENDPOINT);
    data.put("id", licenseKeyId);
    data.put("attributes", objectMapper.convertValue(updateRequest, Map.class));
    requestBody.put("data", data);

    String response = client.patch(LICENSE_KEYS_ENDPOINT + "/" + licenseKeyId, requestBody);
    return parseLicenseKeyResponse(response);
  }

  /**
   * Lists all license keys with optional filtering.
   *
   * @param filter            The filter to apply to the list.
   * @param paginationRequest The pagination parameters.
   * @return A paginated response of LicenseKey objects.
   * @throws IOException If there's an error in the API call.
   */
  public PaginatedResponse<LicenseKey> listLicenseKeys(LicenseKeyFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(LICENSE_KEYS_ENDPOINT + queryParams, paginationRequest);
  }

  private String buildQueryParams(LicenseKeyFilter filter) {
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
    if (filter.getStatus() != null) {
      params.add("filter[status]=" + filter.getStatus());
    }

    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private LicenseKey parseLicenseKeyResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected LicenseKey parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }
    JsonNode attributesNode = itemNode.get("attributes");
    LicenseKey licenseKey = objectMapper.treeToValue(attributesNode, LicenseKey.class);
    licenseKey.setId(itemNode.get("id").asText());
    return licenseKey;
  }
}