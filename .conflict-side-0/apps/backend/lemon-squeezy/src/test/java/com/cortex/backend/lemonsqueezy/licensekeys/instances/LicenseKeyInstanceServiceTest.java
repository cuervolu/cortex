package com.cortex.backend.lemonsqueezy.licensekeys.instances;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.common.PaginatedResponse;
import com.cortex.backend.lemonsqueezy.common.PaginationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LicenseKeyInstanceServiceTest {

  @Mock
  private LemonSqueezyClient client;

  private LicenseKeyInstanceService service;

  @BeforeEach
  void setUp() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    when(client.getObjectMapper()).thenReturn(objectMapper);
    service = new LicenseKeyInstanceService(client);
  }

  @Test
  void getLicenseKeyInstance_shouldReturnInstance() throws IOException {
    String instanceId = "1";
    String jsonResponse = """
        {
          "data": {
            "type": "license-key-instances",
            "id": "1",
            "attributes": {
              "license_key_id": 1,
              "identifier": "f70a79fa-6054-433e-9c1b-6075344292e4",
              "name": "example.com",
              "created_at": "2022-11-14T11:45:39.000000Z",
              "updated_at": "2022-11-14T11:45:39.000000Z"
            }
          }
        }
        """;

    when(client.get("license-key-instances/1")).thenReturn(jsonResponse);

    LicenseKeyInstance instance = service.getLicenseKeyInstance(instanceId);

    assertNotNull(instance);
    assertEquals("1", instance.getId());
    assertEquals(1, instance.getLicenseKeyId());
    assertEquals("f70a79fa-6054-433e-9c1b-6075344292e4", instance.getIdentifier());
    assertEquals("example.com", instance.getName());
    assertNotNull(instance.getCreatedAt());
    assertNotNull(instance.getUpdatedAt());
  }

  @Test
  void listLicenseKeyInstances_shouldReturnPaginatedResponse() throws IOException {
    LicenseKeyInstanceFilter filter = LicenseKeyInstanceFilter.builder().licenseKeyId(1L).build();
    PaginationRequest paginationRequest = new PaginationRequest();
    String jsonResponse = """
        {
          "meta": {
            "page": {
              "currentPage": 1,
              "from": 1,
              "lastPage": 1,
              "perPage": 10,
              "to": 1,
              "total": 1
            }
          },
          "data": [
            {
              "type": "license-key-instances",
              "id": "1",
              "attributes": {
                "license_key_id": 1,
                "identifier": "f70a79fa-6054-433e-9c1b-6075344292e4",
                "name": "example.com",
                "created_at": "2022-11-14T11:45:39.000000Z",
                "updated_at": "2022-11-14T11:45:39.000000Z"
              }
            }
          ]
        }
        """;

    when(client.get("license-key-instances?filter[license_key_id]=1")).thenReturn(jsonResponse);

    PaginatedResponse<LicenseKeyInstance> response = service.listLicenseKeyInstances(filter,
        paginationRequest);

    assertNotNull(response);
    assertNotNull(response.getData());
    assertEquals(1, response.getData().size());

    LicenseKeyInstance instance = response.getData().getFirst();
    assertEquals("1", instance.getId());
    assertEquals(1, instance.getLicenseKeyId());
    assertEquals("f70a79fa-6054-433e-9c1b-6075344292e4", instance.getIdentifier());
    assertEquals("example.com", instance.getName());
    assertNotNull(instance.getCreatedAt());
    assertNotNull(instance.getUpdatedAt());

    assertNotNull(response.getMeta());
    assertEquals(1, response.getMeta().getPage().getCurrentPage());
    assertEquals(1, response.getMeta().getPage().getTotal());
  }

  @Test
  void getLicenseKeyInstance_shouldThrowIOException_whenInvalidResponse() throws IOException {
    String instanceId = "1";
    String jsonResponse = "{ \"invalid\": \"response\" }";

    when(client.get("license-key-instances/1")).thenReturn(jsonResponse);

    assertThrows(IOException.class, () -> service.getLicenseKeyInstance(instanceId));
  }
}