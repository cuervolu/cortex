package com.cortex.backend.security;

import com.cortex.backend.entities.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JwtService jwtService;

  @Value("${application.frontend.url}")
  private String frontendUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
    OAuth2User oAuth2User = oauthToken.getPrincipal();

    User user;
    switch (oAuth2User) {
      case OAuth2UserPrincipal userPrincipal -> user = userPrincipal.user();
      case OidcUserPrincipal oidcUserPrincipal -> user = oidcUserPrincipal.user();
      default -> throw new IllegalStateException("Unexpected OAuth2User type: " + oAuth2User.getClass());
    }

    String jwt = jwtService.generateToken(user);
    String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
        .queryParam("token", jwt)
        .build().toUriString();

    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
  }
}