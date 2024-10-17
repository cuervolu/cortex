package com.cortex.backend.lemonsqueezy.common;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedService<T> {

  protected final LemonSqueezyClient client;
  protected final ObjectMapper objectMapper;

  protected PaginatedService(LemonSqueezyClient client, ObjectMapper objectMapper) {
    this.client = client;
    this.objectMapper = objectMapper;
  }

  protected PaginatedResponse<T> getPaginatedResponse(String endpoint, PaginationRequest paginationRequest) throws IOException {
    String queryParams = buildPaginationQueryParams(paginationRequest);
    String response = client.get(endpoint + queryParams);
    return parsePaginatedResponse(response);
  }

  private String buildPaginationQueryParams(PaginationRequest paginationRequest) {
    List<String> params = new ArrayList<>();
    if (paginationRequest.getPageNumber() != null) {
      params.add("page[number]=" + paginationRequest.getPageNumber());
    }
    if (paginationRequest.getPageSize() != null) {
      params.add("page[size]=" + paginationRequest.getPageSize());
    }
    return params.isEmpty() ? "" : "?" + String.join("&", params);
  }

  private PaginatedResponse<T> parsePaginatedResponse(String response) throws IOException {
    JsonNode rootNode = objectMapper.readTree(response);
    PaginatedResponse<T> paginatedResponse = new PaginatedResponse<>();

    JsonNode dataNode = rootNode.get("data");
    if (dataNode != null && dataNode.isArray()) {
      List<T> items = new ArrayList<>();
      for (JsonNode item : dataNode) {
        items.add(parseItem(item));
      }
      paginatedResponse.setData(items);
    }

    JsonNode metaNode = rootNode.get("meta");
    if (metaNode != null) {
      paginatedResponse.setMeta(objectMapper.treeToValue(metaNode, PaginationMeta.class));
    }

    JsonNode linksNode = rootNode.get("links");
    if (linksNode != null) {
      paginatedResponse.setLinks(objectMapper.treeToValue(linksNode, PaginationLinks.class));
    }

    return paginatedResponse;
  }

  protected abstract T parseItem(JsonNode itemNode) throws IOException;
}