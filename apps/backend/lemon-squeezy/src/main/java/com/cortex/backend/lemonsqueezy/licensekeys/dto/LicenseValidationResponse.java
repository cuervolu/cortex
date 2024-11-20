package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LicenseValidationResponse(
    boolean valid,
    String error,
    @JsonProperty("license_key")
    ValidatedLicenseKey licenseKey,
    ValidatedInstance instance,
    LicenseMetadata meta
) {}
