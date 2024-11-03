package com.cortex.backend.auth.internal;

import com.cortex.backend.auth.internal.infrastructure.BaseOAuth2SuccessHandler;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2AuthenticationSuccessHandler extends BaseOAuth2SuccessHandler {
  @Value("${application.frontend.callback-url}")
  private String frontendUrl;

  public OAuth2AuthenticationSuccessHandler(JwtServiceImpl jwtService, UserRepository userRepository) {
    super(jwtService, userRepository);
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    User user = extractAndUpdateUser(authentication);
    String jwt = jwtService.generateToken(user);

    String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
        .queryParam("token", jwt)
        .build().toUriString();

    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
  }
}