package com.cortex.backend.services;

import com.cortex.backend.config.EmailTemplateName;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import com.resend.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
  private final SpringTemplateEngine templateEngine;
  
  @Value("${RESEND_API_KEY}")
  private String resendApiKey;
  
  public void sendEmail(
      String to,
      String username,
      EmailTemplateName emailTemplate,
      String confirmationUrl,
      String activationCode,
      String subject)
      throws ResendException {
    String templateName;

    if (emailTemplate == null) {
      templateName = "confirm-email";

    } else {
      templateName = emailTemplate.getName();
    }
    Resend resend = new Resend(resendApiKey);
    Map<String, Object> properties = new HashMap<>();
    properties.put("username", username);
    properties.put("confirmationUrl", confirmationUrl);
    properties.put("activation_code", activationCode);
    Context context = new Context();
    context.setVariables(properties);
    String template = templateEngine.process(templateName, context);
    CreateEmailOptions params = CreateEmailOptions.builder()
        .from("Team <team@cuervolu.dev>")
        .to(to)
        .subject(subject)
        .html(template)
        .build();
    try {
      CreateEmailResponse data = resend.emails().send(params);
      log.info("Email sent to: {}", data.getId());
      
    } catch (ResendException e) {
      log.error("Error sending email: {}", e.getMessage());
      throw new ResendException("Error sending email");
    }
    
    
  }
}
