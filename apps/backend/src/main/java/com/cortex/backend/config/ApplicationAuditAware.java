package com.cortex.backend.config;

import com.cortex.backend.entities.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationAuditAware implements AuditorAware<Long> {

  @Override
  public Optional<Long> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }

    User userPrincipal = (User) authentication.getPrincipal();

    return Optional.ofNullable(userPrincipal.getId());
  }
}
