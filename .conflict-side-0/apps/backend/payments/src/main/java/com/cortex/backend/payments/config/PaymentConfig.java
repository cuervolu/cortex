package com.cortex.backend.payments.config;

import com.cortex.backend.lemonsqueezy.LemonSqueezyClient;
import com.cortex.backend.lemonsqueezy.config.LemonSqueezyConfig;
import com.cortex.backend.lemonsqueezy.checkouts.CheckoutService;
import com.cortex.backend.lemonsqueezy.subscriptions.SubscriptionsService;
import com.cortex.backend.lemonsqueezy.subscriptions.invoices.SubscriptionInvoiceService;
import com.cortex.backend.lemonsqueezy.licensekeys.LicenseKeyService;
import com.cortex.backend.lemonsqueezy.licensekeys.instances.LicenseKeyInstanceService;
import com.cortex.backend.lemonsqueezy.webhook.WebhookService;
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