package com.cortex.backend.lemonsqueezy.licensekeys.instances;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record LicenseKeyInstanceFilter(
    @JsonProperty("license_key_id")
    Long licenseKeyId) {

}