package com.cortex.backend.lemonsqueezy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

@Getter
public class LemonSqueezyConfig {

  private final String apiKey;
  private final String baseUrl;
  private final ObjectMapper objectMapper;

  public LemonSqueezyConfig(String apiKey, String baseUrl) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl != null ? baseUrl : "https://api.lemonsqueezy.com/v1/";
    this.objectMapper = configureObjectMapper();
  }

  private ObjectMapper configureObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

}