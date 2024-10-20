package com.cortex.backend.lemonsqueezy.licensekeys.instances;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Represents a Lemon Squeezy license key instance object.
 */
@Data
public class LicenseKeyInstance {

  /**
   * Unique identifier for the license key instance
   */
  private String id;

  /**
   * The ID of the license key this instance belongs to.
   */
  @JsonProperty("license_key_id")
  private long licenseKeyId;

  /**
   * The unique identifier (UUID) for this instance. This is the `instance_id` returned when
   * activating a license key.
   */
  private String identifier;

  /**
   * The name of the license key instance.
   */
  private String name;

  /**
   * An ISO 8601 formatted date-time string indicating when the object was created.
   */
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  /**
   * An ISO 8601 formatted date-time string indicating when the object was last updated.
   */
  @JsonProperty("updated_at")
  private ZonedDateTime updatedAt;
}