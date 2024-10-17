package com.cortex.backend.lemonsqueezy.webhook;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginatedService;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WebhookService extends PaginatedService<Webhook> {

  private static final String WEBHOOKS_ENDPOINT = "webhooks";

  public WebhookService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  /**
   * Creates a new webhook.
   *
   * @param createRequest The request object containing the webhook details.
   * @return The created Webhook object.
   * @throws IOException If there's an error in the API call.
   */
  public Webhook createWebhook(WebhookCreateRequest createRequest) throws IOException {
    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put("type", WEBHOOKS_ENDPOINT);
    data.put("attributes", objectMapper.convertValue(createRequest, Map.class));

    Map<String, Object> relationships = new HashMap<>();
    Map<String, Object> store = new HashMap<>();
    store.put("data", Map.of("type", "stores", "id", createRequest.getStoreId().toString()));
    relationships.put("store", store);
    data.put("relationships", relationships);

    requestBody.put("data", data);

    String response = client.post(WEBHOOKS_ENDPOINT, requestBody);
    return parseWebhookResponse(response);
  }

  /**
   * Retrieves the webhook with the given ID.
   *
   * @param webhookId The ID of the webhook to retrieve.
   * @return The Webhook object.
   * @throws IOException If there's an error in the API call.
   */
  public Webhook getWebhook(String webhookId) throws IOException {
    String response = client.get(WEBHOOKS_ENDPOINT + "/" + webhookId);
    return parseWebhookResponse(response);
  }

  /**
   * Updates a webhook with the given ID.
   *
   * @param webhookId     The ID of the webhook to update.
   * @param updateRequest The update request containing the fields to update.
   * @return The updated Webhook object.
   * @throws IOException If there's an error in the API call.
   */
  public Webhook updateWebhook(String webhookId, WebhookUpdateRequest updateRequest)
      throws IOException {
    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put("type", WEBHOOKS_ENDPOINT);
    data.put("id", webhookId);
    data.put("attributes", objectMapper.convertValue(updateRequest, Map.class));
    requestBody.put("data", data);

    String response = client.patch(WEBHOOKS_ENDPOINT + "/" + webhookId, requestBody);
    return parseWebhookResponse(response);
  }

  /**
   * Deletes a webhook with the given ID.
   *
   * @param webhookId The ID of the webhook to delete.
   * @throws IOException If there's an error in the API call.
   */
  public void deleteWebhook(String webhookId) throws IOException {
    client.delete(WEBHOOKS_ENDPOINT + "/" + webhookId);
  }

  /**
   * Lists all webhooks with optional filtering.
   *
   * @param filter            The filter to apply to the list.
   * @param paginationRequest The pagination parameters.
   * @return A paginated response of Webhook objects.
   * @throws IOException If there's an error in the API call.
   */
  public PaginatedResponse<Webhook> listWebhooks(WebhookFilter filter,
      PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(WEBHOOKS_ENDPOINT + queryParams, paginationRequest);
  }

  private String buildQueryParams(WebhookFilter filter) {
    if (filter == null || filter.storeId() == null) {
      return "";
    }
    return "?filter[store_id]=" + filter.storeId();
  }

  private Webhook parseWebhookResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected Webhook parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }
    JsonNode attributesNode = itemNode.get("attributes");
    Webhook webhook = objectMapper.treeToValue(attributesNode, Webhook.class);
    webhook.setId(itemNode.get("id").asText());
    return webhook;
  }
}