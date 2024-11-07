package com.cortex.backend.payments.api.dto;

public record WebhookResponse(
    boolean success,
    String message
) {
  private static final String SUCCESS_MESSAGE = "Webhook processed successfully";

  public static final WebhookResponse SUCCESS = new WebhookResponse(true, SUCCESS_MESSAGE);

  public static WebhookResponse error(String message) {
    return new WebhookResponse(false, message);
  }
}