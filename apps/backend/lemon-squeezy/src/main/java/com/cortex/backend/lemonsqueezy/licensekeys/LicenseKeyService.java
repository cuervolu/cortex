package com.cortex.backend.lemonsqueezy.licensekeys;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseActivationResponse;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseDeactivationResponse;
import com.cortex.backend.lemonsqueezy.licensekeys.dto.LicenseValidationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LicenseKeyService extends PaginatedService<LicenseKey> {

  // Endpoint constants
  private static final String LICENSE_KEYS_ENDPOINT = "license-keys";
  private static final String VALIDATE_ENDPOINT = "licenses/validate";
  private static final String ACTIVATE_ENDPOINT = "licenses/activate";
  private static final String DEACTIVATE_ENDPOINT = "licenses/deactivate";

  // Field name constants
  private static final String FIELD_TYPE = "type";
  private static final String FIELD_ID = "id";
  private static final String FIELD_DATA = "data";
  private static final String FIELD_ATTRIBUTES = "attributes";

  // Filter field constants
  private static final String FILTER_STORE_ID = "filter[store_id]";
  private static final String FILTER_ORDER_ID = "filter[order_id]";
  private static final String FILTER_ORDER_ITEM_ID = "filter[order_item_id]";
  private static final String FILTER_PRODUCT_ID = "filter[product_id]";
  private static final String FILTER_STATUS = "filter[status]";

  // License API field constants
  private static final String LICENSE_KEY_FIELD = "license_key";
  private static final String FIELD_INSTANCE_NAME = "instance_name";
  private static final String INSTANCE_ID_FIELD = "instance_id";

  // Error messages
  private static final String ERROR_INVALID_RESPONSE = "Invalid response format: missing or invalid 'data' field";
  private static final String ERROR_INVALID_ITEM = "Invalid item format: missing 'attributes' field";

  public LicenseKeyService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Activates a license key with a new instance.
   *
   * @param licenseKey   The license key to activate
   * @param instanceName A label for the new instance to identify it in Lemon Squeezy
   * @return LicenseActivationResponse containing activation results
   * @throws IOException If there's an error in the API call
   */
  public LicenseActivationResponse activateLicenseKey(String licenseKey, String instanceName)
      throws IOException {
    validateRequiredParameter(licenseKey, "License key cannot be null or empty");
    validateRequiredParameter(instanceName, "Instance name cannot be null or empty");

    Map<String, String> formData = new HashMap<>();
    formData.put(LICENSE_KEY_FIELD, licenseKey);
    formData.put(FIELD_INSTANCE_NAME, instanceName);

    String response = client.licenseApiPost(ACTIVATE_ENDPOINT, formData);
    return objectMapper.readValue(response, LicenseActivationResponse.class);
  }

  /**
   * Deactivates a license key instance.
   *
   * @param licenseKey The license key to deactivate
   * @param instanceId The instance ID returned when activating a license key
   * @return LicenseDeactivationResponse containing deactivation results
   * @throws IOException If there's an error in the API call
   */
  public LicenseDeactivationResponse deactivateLicenseKey(String licenseKey, String instanceId)
      throws IOException {
    validateRequiredParameter(licenseKey, "License key cannot be null or empty");
    validateRequiredParameter(instanceId, "Instance ID cannot be null or empty");

    Map<String, String> formData = new HashMap<>();
    formData.put(LICENSE_KEY_FIELD, licenseKey);
    formData.put(INSTANCE_ID_FIELD, instanceId);

    String response = client.licenseApiPost(DEACTIVATE_ENDPOINT, formData);
    return objectMapper.readValue(response, LicenseDeactivationResponse.class);
  }

  /**
   * Validates that a required parameter is not null or empty.
   *
   * @param parameter The parameter to validate
   * @param message   The error message to throw if validation fails
   * @throws IllegalArgumentException if the parameter is null or empty
   */
  private void validateRequiredParameter(String parameter, String message) {
    if (parameter == null || parameter.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  public LicenseValidationResponse validateLicenseKey(String licenseKey, String instanceId)
      throws IOException {
    Map<String, String> formData = new HashMap<>();
    formData.put(LICENSE_KEY_FIELD, licenseKey);

    if (instanceId != null && !instanceId.isEmpty()) {
      formData.put(INSTANCE_ID_FIELD, instanceId);
    }

    String response = client.licenseApiPost(VALIDATE_ENDPOINT, formData);
    return objectMapper.readValue(response, LicenseValidationResponse.class);
  }

  public LicenseKey getLicenseKey(String licenseKeyId) throws IOException {
    String response = client.get(buildEndpointPath(licenseKeyId));
    return parseLicenseKeyResponse(response);
  }

  public LicenseKey updateLicenseKey(String licenseKeyId, LicenseKeyUpdateRequest updateRequest)
      throws IOException {
    Map<String, Object> requestBody = createRequestBody(licenseKeyId, updateRequest);
    String response = client.patch(buildEndpointPath(licenseKeyId), requestBody);
    return parseLicenseKeyResponse(response);
  }

  public PaginatedResponse<LicenseKey> listLicenseKeys(LicenseKeyFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(LICENSE_KEYS_ENDPOINT + queryParams, paginationRequest);
  }

  private Map<String, Object> createRequestBody(String licenseKeyId, Object attributes) {
    Map<String, Object> data = new HashMap<>();
    data.put(FIELD_TYPE, LICENSE_KEYS_ENDPOINT);
    data.put(FIELD_ID, licenseKeyId);
    data.put(FIELD_ATTRIBUTES, objectMapper.convertValue(attributes, Map.class));

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put(FIELD_DATA, data);
    return requestBody;
  }

  private String buildEndpointPath(String licenseKeyId) {
    return LICENSE_KEYS_ENDPOINT + "/" + licenseKeyId;
  }

  private String buildQueryParams(LicenseKeyFilter filter) {
    if (filter == null) {
      return "";
    }

    List<String> params = new ArrayList<>();
    addFilterParam(params, FILTER_STORE_ID, filter.getStoreId());
    addFilterParam(params, FILTER_ORDER_ID, filter.getOrderId());
    addFilterParam(params, FILTER_ORDER_ITEM_ID, filter.getOrderItemId());
    addFilterParam(params, FILTER_PRODUCT_ID, filter.getProductId());
    addFilterParam(params, FILTER_STATUS, filter.getStatus());

    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private void addFilterParam(List<String> params, String paramName, Object value) {
    if (value != null) {
      params.add(paramName + "=" + value);
    }
  }

  private LicenseKey parseLicenseKeyResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get(FIELD_DATA);
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException(ERROR_INVALID_RESPONSE);
    }

    return parseItem(dataNode);
  }

  @Override
  protected LicenseKey parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has(FIELD_ATTRIBUTES)) {
      throw new IOException(ERROR_INVALID_ITEM);
    }
    JsonNode attributesNode = itemNode.get(FIELD_ATTRIBUTES);
    LicenseKey licenseKey = objectMapper.treeToValue(attributesNode, LicenseKey.class);
    licenseKey.setId(itemNode.get(FIELD_ID).asText());
    return licenseKey;
  }
}