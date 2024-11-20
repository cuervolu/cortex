package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.cortex.backend.lemonsqueezy.licensekeys.LicenseKeyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record ValidatedLicenseKey(
    Long id,
    LicenseKeyStatus status,
    String key,
    @JsonProperty("activation_limit")
    Integer activationLimit,
    @JsonProperty("activation_usage")
    Integer activationUsage,
    @JsonProperty("created_at")
    ZonedDateTime createdAt,
    @JsonProperty("expires_at")
    ZonedDateTime expiresAt
) {}