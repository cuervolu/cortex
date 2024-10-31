package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LicenseDeactivationResponse(
    boolean deactivated,
    String error,
    @JsonProperty("license_key")
    ValidatedLicenseKey licenseKey,
    LicenseMetadata meta
) {}