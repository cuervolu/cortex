package com.cortex.backend.lemonsqueezy.subscriptions.invoices;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionInvoiceServiceTest {

  @Mock
  private LemonSqueezyClient client;

  private SubscriptionInvoiceService service;

  @BeforeEach
  void setUp() {
    ObjectMapper objectMapper = new ObjectMapper();
    when(client.getObjectMapper()).thenReturn(objectMapper);
    service = new SubscriptionInvoiceService(client);
  }

  @Test
  void getSubscriptionInvoice_shouldReturnInvoice() throws IOException {
    String invoiceId = "1";
    String mockResponse = "{\"data\":{\"id\":\"1\",\"type\":\"subscription-invoices\",\"attributes\":{\"store_id\":1,\"subscription_id\":1,\"status\":\"paid\",\"total\":999}}}";
    when(client.get(anyString())).thenReturn(mockResponse);

    SubscriptionInvoice result = service.getSubscriptionInvoice(invoiceId);

    assertNotNull(result);
    assertEquals("1", result.getId());
    assertEquals("paid", result.getStatus());
    assertEquals(999, result.getTotal());
    verify(client).get("subscription-invoices/1");
  }

  @Test
  void listSubscriptionInvoices_shouldReturnPaginatedResponse() throws IOException {
    SubscriptionInvoiceFilter filter = SubscriptionInvoiceFilter.builder()
        .storeId(1L)
        .status("paid")
        .refunded(false)
        .subscriptionId(1L)
        .build();
    PaginationRequest paginationRequest = new PaginationRequest();
    String mockResponse = "{\"data\":[{\"id\":\"1\",\"type\":\"subscription-invoices\",\"attributes\":{\"store_id\":1,\"subscription_id\":1,\"status\":\"paid\",\"total\":999}}],\"meta\":{\"page\":{\"currentPage\":1,\"from\":1,\"lastPage\":1,\"perPage\":15,\"to\":1,\"total\":1}}}";
    when(client.get(anyString())).thenReturn(mockResponse);

    PaginatedResponse<SubscriptionInvoice> result = service.listSubscriptionInvoices(filter, paginationRequest);

    assertNotNull(result);
    assertEquals(1, result.getData().size());
    assertEquals("1", result.getData().getFirst().getId());
    verify(client).get(contains("subscription-invoices?filter[store_id]=1&filter[status]=paid&filter[refunded]=false&filter[subscription_id]=1"));
  }

  @Test
  void generateInvoice_shouldReturnDownloadUrl() throws IOException {
    String invoiceId = "1";
    InvoiceGenerationRequest request = InvoiceGenerationRequest.builder()
        .name("John Doe")
        .address("123 Main St")
        .city("Anytown")
        .state("CA")
        .zipCode("12345")
        .country("US")
        .notes("Thank you for your business")
        .build();

    String mockResponse = "{\"meta\":{\"urls\":{\"download_invoice\":\"https://example.com/invoice.pdf\"}}}";
    when(client.post(anyString(), any())).thenReturn(mockResponse);

    String result = service.generateInvoice(invoiceId, request);

    assertEquals("https://example.com/invoice.pdf", result);

    ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
    verify(client).post(eq("subscription-invoices/1/generate-invoice"), argumentCaptor.capture());

    Object capturedArgument = argumentCaptor.getValue();
    assertInstanceOf(Map.class, capturedArgument, "Argument should be an instance of Map");

    @SuppressWarnings("unchecked")
    Map<String, Object> params = (Map<String, Object>) capturedArgument;

    assertEquals("John Doe", params.get("name"));
    assertEquals("123 Main St", params.get("address"));
    assertEquals("Anytown", params.get("city"));
    assertEquals("CA", params.get("state"));
    assertEquals("12345", params.get("zip_code"));
    assertEquals("US", params.get("country"));
    assertEquals("Thank you for your business", params.get("notes"));
  }

  @Test
  void issueRefund_shouldReturnUpdatedInvoice() throws IOException {
    String invoiceId = "1";
    RefundRequest request = RefundRequest.builder().amount(100).build();
    String mockResponse = "{\"data\":{\"id\":\"1\",\"type\":\"subscription-invoices\",\"attributes\":{\"store_id\":1,\"subscription_id\":1,\"status\":\"refunded\",\"total\":899,\"refunded_amount\":100}}}";
    when(client.post(anyString(), any())).thenReturn(mockResponse);

    SubscriptionInvoice result = service.issueRefund(invoiceId, request);

    assertNotNull(result);
    assertEquals("1", result.getId());
    assertEquals("refunded", result.getStatus());
    assertEquals(899, result.getTotal());
    assertEquals(100, result.getRefundedAmount());
    verify(client).post(eq("subscription-invoices/1/refund"), any());
  }
}