package com.cortex.backend.lemonsqueezy;

import com.cortex.backend.lemonsqueezy.config.LemonSqueezyConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class LemonSqueezyClient {

  private final OkHttpClient httpClient;
  private final String baseUrl;
  private final String apiKey;
  private final ObjectMapper objectMapper;

  private static final String LEMON_HEADER = "application/vnd.api+json";

  public LemonSqueezyClient(LemonSqueezyConfig config) {
    this.apiKey = config.getApiKey();
    this.baseUrl = config.getBaseUrl();
    this.httpClient = new OkHttpClient.Builder()
        .addInterceptor(this::addHeaders)
        .build();
    this.objectMapper = new ObjectMapper();
  }

  private Response addHeaders(Interceptor.Chain chain) throws IOException {
    Request originalRequest = chain.request();
    Request newRequest = originalRequest.newBuilder()
        .header("Authorization", "Bearer " + apiKey)
        .header("Accept", LEMON_HEADER)
        .header("Content-Type", LEMON_HEADER)
        .build();
    return chain.proceed(newRequest);
  }

  public String get(String endpoint) throws IOException {
    Request request = new Request.Builder()
        .url(baseUrl + endpoint)
        .build();

    return executeRequest(request);
  }

  public String post(String endpoint, Object body) throws IOException {
    RequestBody requestBody = RequestBody.create(
        objectMapper.writeValueAsString(body),
        MediaType.parse(LEMON_HEADER)
    );

    Request request = new Request.Builder()
        .url(baseUrl + endpoint)
        .post(requestBody)
        .build();

    return executeRequest(request);
  }

  private String executeRequest(Request request) throws IOException {
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      ResponseBody responseBody = response.body();
      if (responseBody == null) {
        throw new IOException("Response body is null");
      }
      return responseBody.string();
    }
  }
}
