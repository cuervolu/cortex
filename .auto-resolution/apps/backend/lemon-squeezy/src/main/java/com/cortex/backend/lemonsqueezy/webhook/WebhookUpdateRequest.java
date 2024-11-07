package com.cortex.backend.lemonsqueezy.webhook;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WebhookUpdateRequest {

  /**
   * A valid URL of the endpoint that should receive webhook events.
   */
  private String url;

  /**
   * An array of webhook event types that should be sent to the webhook endpoint.
   */
  private List<String> events;

  /**
   * A string used by Lemon Squeezy to sign requests for increased security.
   */
  private String secret;
}