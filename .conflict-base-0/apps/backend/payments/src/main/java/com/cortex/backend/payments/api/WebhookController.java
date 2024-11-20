package com.cortex.backend.payments.api;

import com.cortex.backend.payments.api.dto.WebhookResponse;
import com.cortex.backend.payments.internal.WebhookHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/webhooks/lemon-squeezy")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Endpoints for handling Lemon Squeezy webhooks")
public class WebhookController {

  private final WebhookHandler webhookHandler;

  @PostMapping
  @Operation(summary = "Process Lemon Squeezy webhook", description = "Endpoint to receive and process webhooks from Lemon Squeezy")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Webhook processed successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid webhook payload"),
      @ApiResponse(responseCode = "401", description = "Invalid webhook signature"),
      @ApiResponse(responseCode = "500", description = "Internal server error processing webhook")
  })
  public ResponseEntity<WebhookResponse> handleWebhook(
      @RequestBody String payload,
      @RequestHeader("X-Signature") String signature
  ) {
    log.debug("Received webhook from Lemon Squeezy");
    return webhookHandler.processWebhook(payload, signature)
        ? ResponseEntity.ok(WebhookResponse.SUCCESS)
        : ResponseEntity.badRequest().body(WebhookResponse.error("Invalid webhook payload"));
  }

}
