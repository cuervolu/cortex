package com.cortex.backend.infrastructure.api;

import com.cortex.backend.infrastructure.config.EmailTemplateName;
import com.cortex.backend.common.exception.EmailSendingException;
import com.cortex.backend.handler.BusinessErrorCodes;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.resend.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

  private final SpringTemplateEngine templateEngine;

  @Value("${RESEND_API_KEY}")
  private String resendApiKey;

  public void sendEmail(String to, EmailTemplateName emailTemplate,
      Map<String, Object> templateVariables, String subject) {
    String templateName = emailTemplate.getName();
    Resend resend = new Resend(resendApiKey);

    Context context = new Context();
    context.setVariables(templateVariables);
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
      throw new EmailSendingException(BusinessErrorCodes.EMAIL_SENDING_FAILED, e);
    }
  }
}