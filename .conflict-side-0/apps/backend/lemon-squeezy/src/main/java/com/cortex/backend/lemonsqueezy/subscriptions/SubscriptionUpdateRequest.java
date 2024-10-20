package com.cortex.backend.lemonsqueezy.subscriptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionUpdateRequest {
  @JsonProperty("variant_id")
  private Long variantId;

  private Map<String, Object> pause;

  private Boolean cancelled;

  @JsonProperty("trial_ends_at")
  private ZonedDateTime trialEndsAt;

  @JsonProperty("billing_anchor")
  private Integer billingAnchor;

  @JsonProperty("invoice_immediately")
  private Boolean invoiceImmediately;

  @JsonProperty("disable_prorations")
  private Boolean disableProrations;
}