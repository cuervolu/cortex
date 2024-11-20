package com.cortex.backend.lemonsqueezy;

import com.cortex.backend.lemonsqueezy.config.LemonSqueezyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;

public class LemonSqueezyClient {
  private final OkHttpClient httpClient;
  private final String baseUrl;
  private final String apiKey;
  @Getter
  private final ObjectMapper objectMapper;

  // Content type constants
  private static final String LEMON_HEADER = "application/vnd.api+json";
  private static final String JSON_HEADER = "application/json";
  private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

  // Header names
  private static final String HEADER_AUTHORIZATION = "Authorization";
  private static final String HEADER_ACCEPT = "Accept";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";

  // HTTP methods
  private static final String METHOD_POST = "POST";
  private static final String METHOD_PUT = "PUT";
  private static final String METHOD_PATCH = "PATCH";

  // Other constants
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String ERROR_NULL_RESPONSE = "Response body is null";
  private static final String ERROR_UNEXPECTED_CODE = "Unexpected code ";

  public LemonSqueezyClient(LemonSqueezyConfig config) {
    this.apiKey = config.getApiKey();
    this.baseUrl = config.getBaseUrl();
    this.httpClient = new OkHttpClient.Builder()
        .build();
    this.objectMapper = config.getObjectMapper();
  }

  public String get(String endpoint) throws IOException {
    Request request = new Request.Builder()
        .url(baseUrl + endpoint)
        .header(HEADER_AUTHORIZATION, BEARER_PREFIX + apiKey)
        .header(HEADER_ACCEPT, LEMON_HEADER)
        .header(HEADER_CONTENT_TYPE, LEMON_HEADER)
        .build();
    return executeRequest(request);
  }

  public String post(String endpoint, Object body) throws IOException {
    return sendRequestWithBody(endpoint, body, METHOD_POST);
  }

  public String put(String endpoint, Object body) throws IOException {
    return sendRequestWithBody(endpoint, body, METHOD_PUT);
  }

  public String patch(String endpoint, Object body) throws IOException {
    return sendRequestWithBody(endpoint, body, METHOD_PATCH);
  }

  public String delete(String endpoint) throws IOException {
    Request request = new Request.Builder()
        .url(baseUrl + endpoint)
        .header(HEADER_AUTHORIZATION, BEARER_PREFIX + apiKey)
        .header(HEADER_ACCEPT, LEMON_HEADER)
        .header(HEADER_CONTENT_TYPE, LEMON_HEADER)
        .delete()
        .build();
    return executeRequest(request);
  }

  public String licenseApiPost(String endpoint, Map<String, String> formData) throws IOException {
    FormBody.Builder formBodyBuilder = new FormBody.Builder();
    formData.forEach(formBodyBuilder::add);

    Request request = new Request.Builder()
        .url(baseUrl + endpoint)
        .header(HEADER_AUTHORIZATION, BEARER_PREFIX + apiKey)
        .header(HEADER_ACCEPT, JSON_HEADER)
        .header(HEADER_CONTENT_TYPE, FORM_URLENCODED)
        .post(formBodyBuilder.build())
        .build();
    return executeRequest(request);
  }

  private String sendRequestWithBody(String endpoint, Object body, String method) throws IOException {
    RequestBody requestBody = RequestBody.create(
        objectMapper.writeValueAsString(body),
        MediaType.parse(LEMON_HEADER)
    );
    Request request = new Request.Builder()
        .url(baseUrl + endpoint)
        .header(HEADER_AUTHORIZATION, BEARER_PREFIX + apiKey)
        .header(HEADER_ACCEPT, LEMON_HEADER)
        .header(HEADER_CONTENT_TYPE, LEMON_HEADER)
        .method(method, requestBody)
        .build();
    return executeRequest(request);
  }

  private String executeRequest(Request request) throws IOException {
    try (Response response = httpClient.newCall(request).execute()) {
      ResponseBody responseBody = response.body();
      if (responseBody == null) {
        throw new IOException(ERROR_NULL_RESPONSE);
      }
      String responseString = responseBody.string();
      if (!response.isSuccessful()) {
        throw new IOException(ERROR_UNEXPECTED_CODE + response + ". Response body: " + responseString);
      }
      return responseString;
    }
  }
}