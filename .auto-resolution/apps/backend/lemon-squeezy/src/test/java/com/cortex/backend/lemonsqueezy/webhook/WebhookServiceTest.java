package com.cortex.backend.lemonsqueezy.webhook;

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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

  @Mock
  private LemonSqueezyClient lemonSqueezyClient;

  private WebhookService webhookService;

  @BeforeEach
  void setUp() {
    LemonSqueezyConfig config = new LemonSqueezyConfig("test-api-key", null);
    when(lemonSqueezyClient.getObjectMapper()).thenReturn(config.getObjectMapper());
    webhookService = new WebhookService(lemonSqueezyClient);
  }

  @Test
  void createWebhook_shouldReturnWebhook_whenValidRequestProvided() throws IOException {
    // Arrange
    WebhookCreateRequest createRequest = new WebhookCreateRequest("https://example.com/webhook", List.of("order_created"), "secret", false, 1L);
    String jsonResponse = getWebhookJsonResponse();
    when(lemonSqueezyClient.post(anyString(), any())).thenReturn(jsonResponse);

    // Act
    Webhook result = webhookService.createWebhook(createRequest);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("1");
    assertThat(result.getUrl()).isEqualTo("https://example.com/webhook");

    // Verify that the post method is called with the correct parameters
    verify(lemonSqueezyClient).post(eq("webhooks"), argThat(body ->
        body != null && body.toString().contains("https://example.com/webhook") && body.toString().contains("order_created")
    ));
  }

  @Test
  void getWebhook_shouldReturnWebhook_whenValidIdProvided() throws IOException {
    // Arrange
    String webhookId = "1";
    String jsonResponse = getWebhookJsonResponse();
    when(lemonSqueezyClient.get(anyString())).thenReturn(jsonResponse);

    // Act
    Webhook result = webhookService.getWebhook(webhookId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(webhookId);
    assertThat(result.getUrl()).isEqualTo("https://example.com/webhook");
  }

  @Test
  void updateWebhook_shouldReturnUpdatedWebhook_whenValidRequestProvided() throws IOException {
    // Arrange
    String webhookId = "1";
    WebhookUpdateRequest updateRequest = new WebhookUpdateRequest("https://updated.com/webhook", List.of("order_created", "order_refunded"), "new_secret");
    String jsonResponse = getUpdatedWebhookJsonResponse();
    when(lemonSqueezyClient.patch(anyString(), any())).thenReturn(jsonResponse);

    // Act
    Webhook result = webhookService.updateWebhook(webhookId, updateRequest);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(webhookId);
    assertThat(result.getUrl()).isEqualTo("https://updated.com/webhook");
    assertThat(result.getEvents()).containsExactly("order_created", "order_refunded");

    // Verify that the patch method is called with the correct parameters
    verify(lemonSqueezyClient).patch(eq("webhooks/" + webhookId), argThat(body ->
        body != null && body.toString().contains("https://updated.com/webhook") && body.toString().contains("order_refunded")
    ));
  }

  @Test
  void deleteWebhook_shouldCallDeleteMethod_whenValidIdProvided() throws IOException {
    // Arrange
    String webhookId = "1";

    // Act
    webhookService.deleteWebhook(webhookId);

    // Assert
    verify(lemonSqueezyClient).delete("webhooks/" + webhookId);
  }

  @Test
  void listWebhooks_shouldReturnPaginatedResponse_whenFilterAndPaginationProvided() throws IOException {
    // Arrange
    WebhookFilter filter = new WebhookFilter(1L);
    PaginationRequest paginationRequest = new PaginationRequest();
    String jsonResponse = getWebhookListJsonResponse();
    when(lemonSqueezyClient.get(anyString())).thenReturn(jsonResponse);

    // Act
    PaginatedResponse<Webhook> result = webhookService.listWebhooks(filter, paginationRequest);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getData()).hasSize(1);
    assertThat(result.getData().getFirst().getId()).isEqualTo("1");

    // Verify that the correct URL is called with the filter parameters
    verify(lemonSqueezyClient).get(contains("filter[store_id]=1"));
  }

  private String getWebhookJsonResponse() {
    return "{"
        + "\"data\": {"
        + "\"type\": \"webhooks\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"url\": \"https://example.com/webhook\","
        + "\"events\": [\"order_created\"],"
        + "\"last_sent_at\": \"2023-01-01T00:00:00Z\","
        + "\"created_at\": \"2022-01-01T00:00:00Z\","
        + "\"updated_at\": \"2022-01-01T00:00:00Z\","
        + "\"test_mode\": false"
        + "}"
        + "}"
        + "}";
  }

  private String getUpdatedWebhookJsonResponse() {
    return "{"
        + "\"data\": {"
        + "\"type\": \"webhooks\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"url\": \"https://updated.com/webhook\","
        + "\"events\": [\"order_created\", \"order_refunded\"],"
        + "\"last_sent_at\": \"2023-01-01T00:00:00Z\","
        + "\"created_at\": \"2022-01-01T00:00:00Z\","
        + "\"updated_at\": \"2023-01-01T00:00:00Z\","
        + "\"test_mode\": false"
        + "}"
        + "}"
        + "}";
  }

  private String getWebhookListJsonResponse() {
    return "{"
        + "\"data\": [{"
        + "\"type\": \"webhooks\","
        + "\"id\": \"1\","
        + "\"attributes\": {"
        + "\"store_id\": 1,"
        + "\"url\": \"https://example.com/webhook\","
        + "\"events\": [\"order_created\"],"
        + "\"last_sent_at\": \"2023-01-01T00:00:00Z\","
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
}