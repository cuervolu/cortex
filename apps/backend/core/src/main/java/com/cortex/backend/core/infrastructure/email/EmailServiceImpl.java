package com.cortex.backend.core.infrastructure.email;

import com.cortex.backend.core.common.email.EmailService;
import com.cortex.backend.core.common.email.EmailTemplateName;
import com.cortex.backend.core.common.exception.EmailSendingException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final SpringTemplateEngine templateEngine;

  @Value("${RESEND_API_KEY}")
  private String resendApiKey;

  @Override
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
      throw new EmailSendingException("Error sending email");
    }
  }
}