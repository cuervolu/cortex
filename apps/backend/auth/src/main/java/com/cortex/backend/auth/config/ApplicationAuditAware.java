package com.cortex.backend.auth.config;

import com.cortex.backend.core.domain.User;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationAuditAware implements AuditorAware<Long> {
  private static final Long OAUTH2_REGISTRATION_USER_ID = -1L;
  private static final ThreadLocal<Long> CURRENT_AUDITOR = new ThreadLocal<>();

  public static void setCurrentAuditor(Long userId) {
    CURRENT_AUDITOR.set(userId);
  }

  public static void clearCurrentAuditor() {
    CURRENT_AUDITOR.remove();
  }

  @Override
  @NonNull
  public Optional<Long> getCurrentAuditor() {
    Long currentAuditor = CURRENT_AUDITOR.get();
    if (currentAuditor != null) {
      return Optional.of(currentAuditor);
    }

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