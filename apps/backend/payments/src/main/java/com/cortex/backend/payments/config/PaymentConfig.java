package com.cortex.backend.payments.config;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

  @Value("${lemon-squeezy.api.base-url}")
  private String lemonSqueezyBaseUrl;

  @Value("${lemon-squeezy.api.api-key}")
  private String lemonSqueezyApiKey;

  @Bean
  public LemonSqueezyConfig lemonSqueezyConfig() {
    return new LemonSqueezyConfig(lemonSqueezyApiKey, lemonSqueezyBaseUrl);
  }

  @Bean
  public LemonSqueezyClient lemonSqueezyClient(LemonSqueezyConfig config) {
    return new LemonSqueezyClient(config);
  }

  @Bean
  public CheckoutService checkoutService(LemonSqueezyClient client) {
    return new CheckoutService(client);
  }

  @Bean
  public SubscriptionsService subscriptionsService(LemonSqueezyClient client) {
    return new SubscriptionsService(client);
  }

  @Bean
  public SubscriptionInvoiceService subscriptionInvoiceService(LemonSqueezyClient client) {
    return new SubscriptionInvoiceService(client);
  }

  @Bean
  public LicenseKeyService licenseKeyService(LemonSqueezyClient client) {
    return new LicenseKeyService(client);
  }

  @Bean
  public LicenseKeyInstanceService licenseKeyInstanceService(LemonSqueezyClient client) {
    return new LicenseKeyInstanceService(client);
  }
  
  @Bean
  public WebhookService webhookService(LemonSqueezyClient client) {
    return new WebhookService(client);
  }
}