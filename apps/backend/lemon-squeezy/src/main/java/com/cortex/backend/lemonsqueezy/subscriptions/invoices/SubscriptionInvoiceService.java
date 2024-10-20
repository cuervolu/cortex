package com.cortex.backend.lemonsqueezy.subscriptions.invoices;

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

public class SubscriptionInvoiceService extends PaginatedService<SubscriptionInvoice> {

  private static final String SUBSCRIPTION_INVOICES_ENDPOINT = "subscription-invoices";

  public SubscriptionInvoiceService(LemonSqueezyClient client) {
    super(client, client.getObjectMapper());
  }

  public SubscriptionInvoice getSubscriptionInvoice(String invoiceId) throws IOException {
    String response = client.get(SUBSCRIPTION_INVOICES_ENDPOINT + "/" + invoiceId);
    return parseSubscriptionInvoiceResponse(response);
  }

  public PaginatedResponse<SubscriptionInvoice> listSubscriptionInvoices(SubscriptionInvoiceFilter filter, PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildQueryParams(filter);
    return getPaginatedResponse(SUBSCRIPTION_INVOICES_ENDPOINT + queryParams, paginationRequest);
  }

  public String generateInvoice(String invoiceId, InvoiceGenerationRequest request) throws IOException {
    String endpoint = SUBSCRIPTION_INVOICES_ENDPOINT + "/" + invoiceId + "/generate-invoice";
    Map<String, Object> queryParams = new HashMap<>();
    if (request.getName() != null) queryParams.put("name", request.getName());
    if (request.getAddress() != null) queryParams.put("address", request.getAddress());
    if (request.getCity() != null) queryParams.put("city", request.getCity());
    if (request.getState() != null) queryParams.put("state", request.getState());
    if (request.getZipCode() != null) queryParams.put("zip_code", request.getZipCode());
    if (request.getCountry() != null) queryParams.put("country", request.getCountry());
    if (request.getNotes() != null) queryParams.put("notes", request.getNotes());

    String response = client.post(endpoint, queryParams);
    return parseInvoiceGenerationResponse(response);
  }

  public SubscriptionInvoice issueRefund(String invoiceId, RefundRequest request) throws IOException {
    String endpoint = SUBSCRIPTION_INVOICES_ENDPOINT + "/" + invoiceId + "/refund";
    Map<String, Object> requestBody = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    data.put("type", "subscription-invoices");
    data.put("id", invoiceId);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("amount", request.getAmount());
    data.put("attributes", attributes);
    requestBody.put("data", data);

    String response = client.post(endpoint, requestBody);
    return parseSubscriptionInvoiceResponse(response);
  }

  private String parseInvoiceGenerationResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode metaNode = rootNode.get("meta");
    if (metaNode != null && metaNode.has("urls")) {
      JsonNode urlsNode = metaNode.get("urls");
      if (urlsNode.has("download_invoice")) {
        return urlsNode.get("download_invoice").asText();
      }
    }
    throw new IOException("Invalid response format: missing download_invoice URL");
  }

  private String buildQueryParams(SubscriptionInvoiceFilter filter) {
    if (filter == null) {
      return "";
    }

    List<String> params = new ArrayList<>();
    if (filter.getStoreId() != null) {
      params.add("filter[store_id]=" + filter.getStoreId());
    }
    if (filter.getStatus() != null) {
      params.add("filter[status]=" + filter.getStatus());
    }
    if (filter.getRefunded() != null) {
      params.add("filter[refunded]=" + filter.getRefunded());
    }
    if (filter.getSubscriptionId() != null) {
      params.add("filter[subscription_id]=" + filter.getSubscriptionId());
    }

    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private SubscriptionInvoice parseSubscriptionInvoiceResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    JsonNode dataNode = rootNode.get("data");
    if (dataNode == null || !dataNode.isObject()) {
      throw new IOException("Invalid response format: missing or invalid 'data' field");
    }

    return parseItem(dataNode);
  }

  @Override
  protected SubscriptionInvoice parseItem(JsonNode itemNode) throws IOException {
    if (!itemNode.has("attributes")) {
      throw new IOException("Invalid item format: missing 'attributes' field");
    }
    JsonNode attributesNode = itemNode.get("attributes");
    SubscriptionInvoice invoice = objectMapper.treeToValue(attributesNode, SubscriptionInvoice.class);
    invoice.setId(itemNode.get("id").asText());
    return invoice;
  }
}