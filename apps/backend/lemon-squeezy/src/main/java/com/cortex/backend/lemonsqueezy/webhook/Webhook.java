package com.cortex.backend.lemonsqueezy.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Represents a Lemon Squeezy webhook object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Webhook {

  /**
   * Unique identifier for the webhook.
   */
  private String id;

  /**
   * The ID of the store this webhook belongs to.
   */
  @JsonProperty("store_id")
  private long storeId;

  /**
   * The URL that events will be sent to.
   */
  private String url;

  /**
   * An array of events that will be sent.
   */
  private List<String> events;

  /**
   * An ISO 8601 formatted date-time string indicating when the last webhook event was sent.
   * Will be null if no events have been sent yet.
   */
  @JsonProperty("last_sent_at")
  private ZonedDateTime lastSentAt;

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

  /**
   * A boolean indicating if the object was created within test mode.
   */
  @JsonProperty("test_mode")
  private boolean testMode;
}