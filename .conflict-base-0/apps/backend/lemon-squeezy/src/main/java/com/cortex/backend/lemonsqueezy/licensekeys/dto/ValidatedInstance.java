package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record ValidatedInstance(
    String id,
    String name,
    @JsonProperty("created_at")
    ZonedDateTime createdAt
) {}
