package com.cortex.backend.lemonsqueezy.config;

public class LemonSqueezyConfig {
  private final String apiKey;
  private final String baseUrl;

  public LemonSqueezyConfig(String apiKey, String baseUrl) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl != null ? baseUrl : "https://api.lemonsqueezy.com/v1/";
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}