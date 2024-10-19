package com.cortex.backend.lemonsqueezy.subscriptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.cortex.backend.lemonsqueezy.config.LemonSqueezyConfig;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionsServiceTest {

  @Mock
  private LemonSqueezyClient lemonSqueezyClient;

  private SubscriptionsService subscriptionsService;

  @BeforeEach
  void setUp() {
    LemonSqueezyConfig config = new LemonSqueezyConfig("test-api-key", null);
    when(lemonSqueezyClient.getObjectMapper()).thenReturn(config.getObjectMapper());
    subscriptionsService = new SubscriptionsService(lemonSqueezyClient);
  }

  @Test
  void getSubscription_shouldReturnSubscription_whenValidIdProvided() throws IOException {
    // Arrange
    String subscriptionId = "1";
    String jsonResponse = getSubscriptionJsonResponse();
    when(lemonSqueezyClient.get(anyString())).thenReturn(jsonResponse);

    // Act
    Subscription result = subscriptionsService.getSubscription(subscriptionId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(subscriptionId);
    assertThat(result.getStatus()).isEqualTo("active");
  }

  @Test
  void listSubscriptions_shouldReturnPaginatedResponse_whenFilterAndPaginationProvided() throws IOException {
    // Arrange
    SubscriptionFilter filter = SubscriptionFilter.builder()
        .storeId(1L)
        .orderId(2L)
        .orderItemId(3L)
        .productId(4L)
        .variantId(5L)
        .userEmail("test@example.com")
        .status("active")
        .build();
    PaginationRequest paginationRequest = new PaginationRequest();
    String jsonResponse = getSubscriptionListJsonResponse();
    when(lemonSqueezyClient.get(anyString())).thenReturn(jsonResponse);

    // Act
    PaginatedResponse<Subscription> result = subscriptionsService.listSubscriptions(filter, paginationRequest);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getId()).isEqualTo("1");

    // Verify that the correct URL is called with the filter parameters
    verify(lemonSqueezyClient).get(contains("filter[store_id]=1&filter[order_id]=2&filter[order_item_id]=3&filter[product_id]=4&filter[variant_id]=5&filter[user_email]=test@example.com&filter[status]=active"));
  }

  @Test
  void updateSubscription_shouldReturnUpdatedSubscription_whenValidRequestProvided() throws IOException {
    // Arrange
    String subscriptionId = "1";
    SubscriptionUpdateRequest updateRequest = SubscriptionUpdateRequest.builder()
        .variantId(2L)
        .pause(Map.of("mode", "void"))
        .cancelled(false)
        .trialEndsAt(ZonedDateTime.now().plusDays(7))
        .billingAnchor(15)
        .invoiceImmediately(true)
        .disableProrations(false)
        .build();
    String jsonResponse = getSubscriptionJsonResponse();
    when(lemonSqueezyClient.patch(anyString(), any())).thenReturn(jsonResponse);

    // Act
    Subscription result = subscriptionsService.updateSubscription(subscriptionId, updateRequest);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(subscriptionId);

    // Verify that the patch method is called with the correct parameters
    verify(lemonSqueezyClient).patch(eq("subscriptions/" + subscriptionId), argThat(body -> {
      // Here you can add more specific checks on the request body if needed
      return body != null && body.toString().contains("variant_id") && body.toString().contains("pause");
    }));
  }

  @Test
  void cancelSubscription_shouldReturnCancelledSubscription_whenValidIdProvided() throws IOException {
    // Arrange
    String subscriptionId = "1";
    String jsonResponse = getCancelledSubscriptionJsonResponse();
    when(lemonSqueezyClient.delete(anyString())).thenReturn(jsonResponse);

    // Act
    Subscription result = subscriptionsService.cancelSubscription(subscriptionId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(subscriptionId);
    assertThat(result.getStatus()).isEqualTo("cancelled");
  }

  private String getSubscriptionJsonResponse() {
    return "{"
        + "\"data\": {"
        + "\"type\": \"subscriptions\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"customer_id\": 1,"
        + "\"order_id\": 1,"
        + "\"order_item_id\": 1,"
        + "\"product_id\": 1,"
        + "\"variant_id\": 1,"
        + "\"product_name\": \"Example Product\","
        + "\"variant_name\": \"Example Variant\","
        + "\"user_name\": \"John Doe\","
        + "\"user_email\": \"john@example.com\","
        + "\"status\": \"active\","
        + "\"status_formatted\": \"Active\","
        + "\"pause\": null,"
        + "\"cancelled\": false,"
        + "\"trial_ends_at\": null,"
        + "\"billing_anchor\": 1,"
        + "\"urls\": {},"
        + "\"renews_at\": \"2023-01-01T00:00:00Z\","
        + "\"ends_at\": null,"
        + "\"created_at\": \"2022-01-01T00:00:00Z\","
        + "\"updated_at\": \"2022-01-01T00:00:00Z\","
        + "\"test_mode\": false"
        + "}"
        + "}"
        + "}";
  }

  private String getSubscriptionListJsonResponse() {
    return "{"
        + "\"data\": [{"
        + "\"type\": \"subscriptions\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"customer_id\": 1,"
        + "\"order_id\": 1,"
        + "\"order_item_id\": 1,"
        + "\"product_id\": 1,"
        + "\"variant_id\": 1,"
        + "\"product_name\": \"Example Product\","
        + "\"variant_name\": \"Example Variant\","
        + "\"user_name\": \"John Doe\","
        + "\"user_email\": \"john@example.com\","
        + "\"status\": \"active\","
        + "\"status_formatted\": \"Active\","
        + "\"pause\": null,"
        + "\"cancelled\": false,"
        + "\"trial_ends_at\": null,"
        + "\"billing_anchor\": 1,"
        + "\"urls\": {},"
        + "\"renews_at\": \"2023-01-01T00:00:00Z\","
        + "\"ends_at\": null,"
        + "\"created_at\": \"2022-01-01T00:00:00Z\","
        + "\"updated_at\": \"2022-01-01T00:00:00Z\","
        + "\"test_mode\": false"
        + "}"
        + "}],"
        + "\"meta\": {"
        + "\"page\": {"
        + "\"currentPage\": 1,"
        + "\"from\": 1,"
        + "\"lastPage\": 1,"
        + "\"perPage\": 10,"
        + "\"to\": 1,"
        + "\"total\": 1"
        + "}"
        + "},"
        + "\"links\": {}"
        + "}";
  }

  private String getCancelledSubscriptionJsonResponse() {
    return "{"
        + "\"data\": {"
        + "\"type\": \"subscriptions\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"customer_id\": 1,"
        + "\"order_id\": 1,"
        + "\"order_item_id\": 1,"
        + "\"product_id\": 1,"
        + "\"variant_id\": 1,"
        + "\"product_name\": \"Example Product\","
        + "\"variant_name\": \"Example Variant\","
        + "\"user_name\": \"John Doe\","
        + "\"user_email\": \"john@example.com\","
        + "\"status\": \"cancelled\","
        + "\"status_formatted\": \"Cancelled\","
        + "\"pause\": null,"
        + "\"cancelled\": true,"
        + "\"trial_ends_at\": null,"
        + "\"billing_anchor\": 1,"
        + "\"urls\": {},"
        + "\"renews_at\": null,"
        + "\"ends_at\": \"2023-01-01T00:00:00Z\","
        + "\"created_at\": \"2022-01-01T00:00:00Z\","
        + "\"updated_at\": \"2022-01-01T00:00:00Z\","
        + "\"test_mode\": false"
        + "}"
        + "}"
        + "}";
  }
}