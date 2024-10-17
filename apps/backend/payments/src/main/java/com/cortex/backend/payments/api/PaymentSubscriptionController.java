package com.cortex.backend.payments.api;

import com.cortex.backend.payments.internal.PaymentService;
import com.cortex.backend.payments.internal.SubscriptionService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment and Subscription", description = "Endpoints for managing payments and subscriptions")
public class PaymentSubscriptionController {

  private final PaymentService paymentService;
  private final SubscriptionService subscriptionService;

  @PostMapping("/preference")
  @Operation(summary = "Create payment preference", description = "Creates a payment preference for a user and plan")
  public ResponseEntity<Preference> createPaymentPreference(
      @Parameter(description = "User ID") @RequestParam Long userId,
      @Parameter(description = "Plan ID") @RequestParam Long planId)
      throws MPException, MPApiException {
    Preference preference = paymentService.createPaymentPreference(userId, planId);
    return ResponseEntity.ok(preference);
  }

  @PostMapping("/process/{paymentId}")
  @Operation(summary = "Process payment", description = "Processes a payment after it's completed")
  public ResponseEntity<Void> processPayment(
      @Parameter(description = "Payment ID") @PathVariable Long paymentId)
      throws MPException, MPApiException {
    paymentService.processPayment(paymentId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{paymentId}")
  @Operation(summary = "Get payment info", description = "Retrieves information about a specific payment")
  public ResponseEntity<Payment> getPaymentInfo(
      @Parameter(description = "Payment ID") @PathVariable Long paymentId)
      throws MPException, MPApiException {
    Payment payment = paymentService.getPaymentInfo(paymentId);
    return ResponseEntity.ok(payment);
  }

  @PostMapping("/refund/{paymentId}")
  @Operation(summary = "Refund payment", description = "Refunds a payment and cancels associated subscriptions")
  public ResponseEntity<Void> refundPayment(
      @Parameter(description = "Payment ID") @PathVariable Long paymentId,
      @Parameter(description = "Refund reason") @RequestParam String reason)
      throws MPException, MPApiException {
    paymentService.refundPayment(paymentId, reason);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/subscriptions")
  @Operation(summary = "Create subscription", description = "Creates a subscription for a user and plan")
  public ResponseEntity<Preference> createSubscription(
      @Parameter(description = "User ID") @RequestParam Long userId,
      @Parameter(description = "Plan ID") @RequestParam Long planId)
      throws MPException, MPApiException {
    Preference preference = subscriptionService.createSubscription(userId, planId);
    return ResponseEntity.ok(preference);
  }

  @PostMapping("/subscriptions/activate")
  @Operation(summary = "Activate subscription", description = "Activates a subscription after payment is completed")
  public ResponseEntity<Void> activateSubscription(
      @Parameter(description = "Preference ID") @RequestParam String preferenceId) {
    subscriptionService.activateSubscription(preferenceId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/subscriptions/{subscriptionId}/cancel")
  @Operation(summary = "Cancel subscription", description = "Cancels an active subscription")
  public ResponseEntity<Void> cancelSubscription(
      @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId) {
    subscriptionService.cancelSubscription(subscriptionId);
    return ResponseEntity.ok().build();
  }
}
