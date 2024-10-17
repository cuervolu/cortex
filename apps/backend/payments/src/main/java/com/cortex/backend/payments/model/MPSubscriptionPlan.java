package com.cortex.backend.payments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class MPSubscriptionPlan {

  private String id;

  @JsonProperty("application_id")
  private Long applicationId;

  @JsonProperty("collector_id")
  private Long collectorId;
  private String reason;

  @JsonProperty("auto_recurring")
  private AutoRecurring autoRecurring;

  @JsonProperty("payment_methods_allowed")
  private PaymentMethodsAllowed paymentMethodsAllowed;
  @JsonProperty("back_url")
  private String backUrl;

  @JsonProperty("external_reference")
  private String externalReference;

  @JsonProperty("init_point")
  private String initPoint;

  @JsonProperty("date_created")
  private OffsetDateTime dateCreated;

  @JsonProperty("last_modified")
  private OffsetDateTime lastModified;
  
  private String status;

}
