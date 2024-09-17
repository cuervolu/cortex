package com.cortex.backend.infrastructure.config;

import com.cortex.backend.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationAuditAware implements AuditorAware<Long> {
  private static final Long OAUTH2_REGISTRATION_USER_ID = -1L;

  @Override
  @NonNull
  public Optional<Long> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken) {
      return Optional.of(OAUTH2_REGISTRATION_USER_ID);
    }

    if (authentication.getPrincipal() instanceof User user) {
      return Optional.of(user.getId());
    }

    return Optional.of(OAUTH2_REGISTRATION_USER_ID);
  }
}
