package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ValidatedInstance(
    String id,
    String name,
    @JsonProperty("created_at")
    ZonedDateTime createdAt
) {}
