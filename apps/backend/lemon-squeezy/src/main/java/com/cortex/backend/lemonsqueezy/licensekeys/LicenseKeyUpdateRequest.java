package com.cortex.backend.lemonsqueezy.licensekeys;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LicenseKeyUpdateRequest {

  /**
   * The activation limit of this license key. Null means "unlimited".
   */
  @JsonProperty("activation_limit")
  private Integer activationLimit;

  /**
   * An ISO 8601 formatted date-time string indicating when the license key expires.
   * Can be null if the license key is perpetual.
   */
  @JsonProperty("expires_at")
  private ZonedDateTime expiresAt;

  /**
   * If true, the license key will have "disabled" status.
   */
  private Boolean disabled;
}