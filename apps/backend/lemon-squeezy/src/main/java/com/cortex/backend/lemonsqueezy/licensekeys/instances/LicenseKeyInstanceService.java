package com.cortex.backend.lemonsqueezy.licensekeys.instances;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class LicenseKeyInstanceService extends PaginatedService<LicenseKeyInstance> {

  private static final String LICENSE_KEY_INSTANCES_ENDPOINT = "license-key-instances";

  public LicenseKeyInstanceService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Retrieves the license key instance with the given ID.
   *
   * @param instanceId The ID of the license key instance to retrieve.
   * @return A LicenseKeyInstance object.
   * @throws IOException If there's an error in the API call.
   */
  public LicenseKeyInstance getLicenseKeyInstance(String instanceId) throws IOException {
    String response = client.get(LICENSE_KEY_INSTANCES_ENDPOINT + "/" + instanceId);
    return parseLicenseKeyInstanceResponse(response);
  }

  /**
   * Lists all license key instances with optional filtering.
   *
   * @param filter            The filter to apply to the list.
   * @param paginationRequest The pagination parameters.
   * @return A paginated response of LicenseKeyInstance objects.
   * @throws IOException If there's an error in the API call.
   */
  public PaginatedResponse<LicenseKeyInstance> listLicenseKeyInstances(
      LicenseKeyInstanceFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(LICENSE_KEY_INSTANCES_ENDPOINT + queryParams, paginationRequest);
  }

  private String buildQueryParams(LicenseKeyInstanceFilter filter) {
    if (filter == null || filter.licenseKeyId() == null) {
      return "";
    }
    return "?filter[license_key_id]=" + filter.licenseKeyId();
  }

  private LicenseKeyInstance parseLicenseKeyInstanceResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected LicenseKeyInstance parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }
    JsonNode attributesNode = itemNode.get("attributes");
    LicenseKeyInstance instance = objectMapper.treeToValue(attributesNode,
        LicenseKeyInstance.class);
    instance.setId(itemNode.get("id").asText());
    return instance;
  }
}