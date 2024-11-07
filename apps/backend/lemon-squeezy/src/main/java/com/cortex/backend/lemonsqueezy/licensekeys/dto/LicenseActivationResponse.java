package com.cortex.backend.lemonsqueezy.licensekeys.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record LicenseActivationResponse(
    boolean activated,
    String error,
    @JsonProperty("license_key")
    ValidatedLicenseKey licenseKey,
    ValidatedInstance instance,
    LicenseMetadata meta
) {}