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

  @Value("${application.mercadopago.access-token}")
  private String mercadoPagoAccessToken;

  @PostConstruct // This method is executed after the bean has been created
  public void initialize() {
    MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
  }

  @Bean
  public PaymentClient paymentClient() {
    return new PaymentClient();
  }

  @Bean
  public PreferenceClient preferenceClient() {
    return new PreferenceClient();
  }


}
