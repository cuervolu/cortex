package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ValidatedLicenseKey(
    String id,
    String status,
    String key,
    @JsonProperty("activation_limit")
    Integer activationLimit,
    @JsonProperty("activation_usage")
    Integer activationUsage,
    @JsonProperty("created_at")
    ZonedDateTime createdAt,
    @JsonProperty("expires_at")
    ZonedDateTime expiresAt,
    @JsonProperty("test_mode")
    Boolean testMode
) {}