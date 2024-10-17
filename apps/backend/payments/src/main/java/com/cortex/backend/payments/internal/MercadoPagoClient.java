package com.cortex.backend.payments.internal;

import com.cortex.backend.core.domain.SubscriptionPlan;
import com.cortex.backend.payments.model.AutoRecurring;
import com.cortex.backend.payments.model.MPSubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class MercadoPagoClient {

  private final RestTemplate restTemplate;

  @Value("${application.mercadopago.access-token}")
  private String accessToken;

  @Value("${application.mercadopago.api.base-url}")
  private String baseUrl;

  @Value("${application.frontend.base-url}")
  private String frontendUrl;

  @Value("${application.mercadopago.subscription.urls.success}")
  private String successUrl;

  private static final String PREAPPROVAL_PLAN = "/preapproval_plan/";

  private HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + accessToken);
    return headers;
  }

  private MPSubscriptionPlan createMPPlan(SubscriptionPlan plan) {
    MPSubscriptionPlan mpPlan = new MPSubscriptionPlan();
    mpPlan.setReason(plan.getName());
    mpPlan.setBackUrl(successUrl);

    AutoRecurring autoRecurring = new AutoRecurring();
    autoRecurring.setFrequency(plan.getIntervalCount());
    autoRecurring.setFrequencyType(plan.getIntervalUnit().toString().toLowerCase());
    autoRecurring.setTransactionAmount(plan.getPrice());
    autoRecurring.setCurrencyId(plan.getCurrencyId());

    mpPlan.setAutoRecurring(autoRecurring);
    return mpPlan;
  }

  public MPSubscriptionPlan createPlan(SubscriptionPlan plan) {
    String url = baseUrl + PREAPPROVAL_PLAN;
    HttpEntity<MPSubscriptionPlan> request = new HttpEntity<>(createMPPlan(plan), createHeaders());
    return restTemplate.exchange(url, HttpMethod.POST, request, MPSubscriptionPlan.class).getBody();
  }

  public MPSubscriptionPlan updatePlan(String mercadoPagoPlanId, SubscriptionPlan plan) {
    String url = baseUrl + PREAPPROVAL_PLAN + mercadoPagoPlanId;
    HttpEntity<MPSubscriptionPlan> request = new HttpEntity<>(createMPPlan(plan), createHeaders());
    return restTemplate.exchange(url, HttpMethod.PUT, request, MPSubscriptionPlan.class).getBody();
  }

  public void deletePlan(String mercadoPagoPlanId) {
    String url = baseUrl + PREAPPROVAL_PLAN + mercadoPagoPlanId;
    HttpEntity<String> request = new HttpEntity<>(createHeaders());
    restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);
  }

  public MPSubscriptionPlan getPlan(String mercadoPagoPlanId) {
    String url = baseUrl + PREAPPROVAL_PLAN + mercadoPagoPlanId;
    HttpEntity<String> request = new HttpEntity<>(createHeaders());
    return restTemplate.exchange(url, HttpMethod.GET, request, MPSubscriptionPlan.class).getBody();
  }
}