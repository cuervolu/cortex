package com.cortex.backend.lemonsqueezy.licensekeys;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenseKeyServiceTest {

  @Mock
  private LemonSqueezyClient client;

  private LicenseKeyService licenseKeyService;

  @BeforeEach
  void setUp() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    when(client.getObjectMapper()).thenReturn(objectMapper);
    licenseKeyService = new LicenseKeyService(client);
  }

  @Test
  void testGetLicenseKey() throws IOException {
    // Arrange
    String licenseKeyId = "1";
    String mockResponse = "{\"data\":{\"type\":\"license-keys\",\"id\":\"1\",\"attributes\":{\"key\":\"test-key\"}}}";
    when(client.get(anyString())).thenReturn(mockResponse);

    // Act
    LicenseKey result = licenseKeyService.getLicenseKey(licenseKeyId);

    // Assert
    assertNotNull(result);
    assertEquals("1", result.getId());
    assertEquals("test-key", result.getKey());
    verify(client).get("license-keys/1");
  }

  @Test
  void testUpdateLicenseKey() throws IOException {
    // Arrange
    String licenseKeyId = "1";
    LicenseKeyUpdateRequest updateRequest = LicenseKeyUpdateRequest.builder()
        .activationLimit(5)
        .expiresAt(ZonedDateTime.now())
        .disabled(false)
        .build();
    String mockResponse = "{\"data\":{\"type\":\"license-keys\",\"id\":\"1\",\"attributes\":{\"key\":\"updated-key\"}}}";
    when(client.patch(anyString(), any())).thenReturn(mockResponse);

    // Act
    LicenseKey result = licenseKeyService.updateLicenseKey(licenseKeyId, updateRequest);

    // Assert
    assertNotNull(result);
    assertEquals("1", result.getId());
    assertEquals("updated-key", result.getKey());
    verify(client).patch(eq("license-keys/1"), any());
  }

  @Test
  void testListLicenseKeys() throws IOException {
    // Arrange
    LicenseKeyFilter filter = LicenseKeyFilter.builder()
        .storeId(1L)
        .status("active")
        .build();
    PaginationRequest paginationRequest = new PaginationRequest();
    paginationRequest.setPageNumber(1);
    paginationRequest.setPageSize(10);
    String mockResponse = "{\"data\":[{\"type\":\"license-keys\",\"id\":\"1\",\"attributes\":{\"key\":\"key1\"}},{\"type\":\"license-keys\",\"id\":\"2\",\"attributes\":{\"key\":\"key2\"}}]}";
    when(client.get(anyString())).thenReturn(mockResponse);

    // Act
    PaginatedResponse<LicenseKey> result = licenseKeyService.listLicenseKeys(filter, paginationRequest);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getData().size());
    assertEquals("1", result.getData().get(0).getId());
    assertEquals("key1", result.getData().get(0).getKey());
    assertEquals("2", result.getData().get(1).getId());
    assertEquals("key2", result.getData().get(1).getKey());
    
    verify(client).get(argThat(url ->
        url.startsWith("license-keys?") &&
            url.contains("filter[store_id]=1") &&
            url.contains("filter[status]=active") &&
            url.contains("page[number]=1") &&
            url.contains("page[size]=10")
    ));
  }
  @Test
  void testListLicenseKeysWithNullFilter() throws IOException {
    // Arrange
    PaginationRequest paginationRequest = new PaginationRequest();
    paginationRequest.setPageNumber(1);
    paginationRequest.setPageSize(10);
    String mockResponse = "{\"data\":[{\"type\":\"license-keys\",\"id\":\"1\",\"attributes\":{\"key\":\"key1\"}}]}";
    when(client.get(anyString())).thenReturn(mockResponse);

    // Act
    PaginatedResponse<LicenseKey> result = licenseKeyService.listLicenseKeys(null, paginationRequest);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getData().size());
    assertEquals("1", result.getData().getFirst().getId());
    assertEquals("key1", result.getData().getFirst().getKey());
    verify(client).get("license-keys?page[number]=1&page[size]=10");
  }
  @Test
  void testInvalidResponseFormat() throws IOException {
    // Arrange
    String licenseKeyId = "1";
    String mockResponse = "{\"invalid\":\"response\"}";
    when(client.get(anyString())).thenReturn(mockResponse);

    // Act & Assert
    assertThrows(IOException.class, () -> licenseKeyService.getLicenseKey(licenseKeyId));
  }
}