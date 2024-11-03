package com.cortex.backend.auth.internal.infrastructure;

import com.cortex.backend.auth.internal.JwtServiceImpl;
import com.cortex.backend.auth.internal.OAuth2UserPrincipal;
import com.cortex.backend.auth.internal.OidcUserPrincipal;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
public abstract class BaseOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  protected final JwtServiceImpl jwtService;
  protected final UserRepository userRepository;

  protected User extractAndUpdateUser(Authentication authentication) {
    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
    OAuth2User oauth2User = oauthToken.getPrincipal();

    User user = switch (oauth2User) {
      case OAuth2UserPrincipal userPrincipal -> userPrincipal.user();
      case OidcUserPrincipal oidcUserPrincipal -> oidcUserPrincipal.user();
      default -> throw new IllegalStateException(
          "Unexpected OAuth2User type: " + oauth2User.getClass());
    };

    user.updateLoginStats();
    return userRepository.save(user);
  }

  protected boolean isDesktopClient(HttpServletRequest request) {
    String clientType = request.getParameter("client_type");
    return "desktop".equals(clientType);
  }
}