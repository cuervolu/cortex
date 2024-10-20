package com.cortex.backend.lemonsqueezy.checkouts;

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
class CheckoutServiceTest {

  @Mock
  private LemonSqueezyClient lemonSqueezyClient;

  private CheckoutService checkoutService;

  @BeforeEach
  void setUp() {
    LemonSqueezyConfig config = new LemonSqueezyConfig("test-api-key", null);
    when(lemonSqueezyClient.getObjectMapper()).thenReturn(config.getObjectMapper());
    checkoutService = new CheckoutService(lemonSqueezyClient);
  }

  @Test
  void createCheckout_shouldReturnCheckout_whenValidRequestProvided() throws IOException {
    // Arrange
    CheckoutRequest request = CheckoutRequest.builder()
        .storeId(1L)
        .variantId(2L)
        .customPrice(1000)
        .productOptions(Map.of("name", "Test Product"))
        .checkoutOptions(Map.of("logo", "https://example.com/logo.png"))
        .checkoutData(Map.of("email", "test@example.com"))
        .expiresAt(ZonedDateTime.now().plusDays(7))
        .build();
    String jsonResponse = getCheckoutJsonResponse();
    when(lemonSqueezyClient.post(anyString(), any())).thenReturn(jsonResponse);

    // Act
    Checkout result = checkoutService.createCheckout(request);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("1");
    assertThat(result.getStoreId()).isEqualTo(1);
    assertThat(result.getVariantId()).isEqualTo(2);
    assertThat(result.getCustomPrice()).isEqualTo(1000);

    // Verify that the post method is called with the correct parameters
    verify(lemonSqueezyClient).post(eq("checkouts"), argThat(body ->
        body != null && body.toString().contains("store_id") && body.toString().contains("variant_id")
    ));
  }

  @Test
  void getCheckout_shouldReturnCheckout_whenValidIdProvided() throws IOException {
    // Arrange
    String checkoutId = "1";
    String jsonResponse = getCheckoutJsonResponse();
    when(lemonSqueezyClient.get(anyString())).thenReturn(jsonResponse);

    // Act
    Checkout result = checkoutService.getCheckout(checkoutId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(checkoutId);
    assertThat(result.getStoreId()).isEqualTo(1);
    assertThat(result.getVariantId()).isEqualTo(2);
    assertThat(result.getCustomPrice()).isEqualTo(1000);
  }

  @Test
  void listCheckouts_shouldReturnPaginatedResponse_whenFilterAndPaginationProvided() throws IOException {
    // Arrange
    Long storeId = 1L;
    Long variantId = 2L;
    PaginationRequest paginationRequest = new PaginationRequest();
    String jsonResponse = getCheckoutListJsonResponse();
    when(lemonSqueezyClient.get(anyString())).thenReturn(jsonResponse);

    // Act
    PaginatedResponse<Checkout> result = checkoutService.listCheckouts(storeId, variantId, paginationRequest);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getId()).isEqualTo("1");
    assertThat(result.getData().getFirst().getCustomPrice()).isEqualTo(1000);

    // Verify that the correct URL is called with the filter parameters
    verify(lemonSqueezyClient).get(contains("filter[store_id]=1&filter[variant_id]=2"));
  }

  private String getCheckoutJsonResponse() {
    return "{"
        + "\"data\": {"
        + "\"type\": \"checkouts\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"variant_id\": 2,"
        + "\"custom_price\": 1000,"
        + "\"product_options\": {\"name\": \"Test Product\"},"
        + "\"checkout_options\": {\"logo\": \"https://example.com/logo.png\"},"
        + "\"checkout_data\": {\"email\": \"test@example.com\"},"
        + "\"expires_at\": \"2023-01-08T00:00:00Z\","
        + "\"created_at\": \"2023-01-01T00:00:00Z\","
        + "\"updated_at\": \"2023-01-01T00:00:00Z\","
        + "\"test_mode\": false,"
        + "\"url\": \"https://example.com/checkout\""
        + "}"
        + "}"
        + "}";
  }

  private String getCheckoutListJsonResponse() {
    return "{"
        + "\"data\": [{"
        + "\"type\": \"checkouts\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"variant_id\": 2,"
        + "\"custom_price\": 1000,"
        + "\"product_options\": {\"name\": \"Test Product\"},"
        + "\"checkout_options\": {\"logo\": \"https://example.com/logo.png\"},"
        + "\"checkout_data\": {\"email\": \"test@example.com\"},"
        + "\"expires_at\": \"2023-01-08T00:00:00Z\","
        + "\"created_at\": \"2023-01-01T00:00:00Z\","
        + "\"updated_at\": \"2023-01-01T00:00:00Z\","
        + "\"test_mode\": false,"
        + "\"url\": \"https://example.com/checkout\""
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
}