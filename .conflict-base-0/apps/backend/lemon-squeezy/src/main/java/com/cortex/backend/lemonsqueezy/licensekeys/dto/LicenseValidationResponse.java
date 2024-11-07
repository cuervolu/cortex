package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record LicenseValidationResponse(
    boolean valid,
    String error,
    @JsonProperty("license_key")
    ValidatedLicenseKey licenseKey,
    ValidatedInstance instance,
    LicenseMetadata meta
) {}
