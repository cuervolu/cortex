package com.cortex.backend.auth.internal;

import com.cortex.backend.auth.internal.infrastructure.BaseOAuth2SuccessHandler;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class PKCEAwareOAuth2SuccessHandler extends BaseOAuth2SuccessHandler {

  private final PKCEAuthorizationRequestRepository pkceAuthorizationRequestRepository;

  public PKCEAwareOAuth2SuccessHandler(
      JwtServiceImpl jwtService,
      UserRepository userRepository,
      PKCEAuthorizationRequestRepository pkceAuthorizationRequestRepository) {
    super(jwtService, userRepository);
    this.pkceAuthorizationRequestRepository = pkceAuthorizationRequestRepository;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    OAuth2AuthorizationRequest authRequest = pkceAuthorizationRequestRepository
        .loadAuthorizationRequest(request);

    if (authRequest == null || authRequest.getAdditionalParameters() == null) {
      log.warn("No authorization request found, using default redirect");
      super.onAuthenticationSuccess(request, response, authentication);
      return;
    }

    String clientType = (String) authRequest.getAdditionalParameters().get("client_type");
    String redirectUri = (String) authRequest.getAdditionalParameters().get("redirect_uri");

    log.debug("Client type: {}, Redirect URI: {}", clientType, redirectUri);

    if (!"desktop".equals(clientType) || redirectUri == null) {
      log.debug("Not a desktop client or no redirect URI, using default handler");
      super.onAuthenticationSuccess(request, response, authentication);
      return;
    }

    User user = extractAndUpdateUser(authentication);
    String jwt = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    if (!user.isProfileComplete()) {
      log.warn("Incomplete profile for desktop user: {}", user.getUsername());
      String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
          .queryParam("token", jwt)
          .queryParam("requiresProfile", "true")
          .build().toUriString();

      log.debug("Redirecting desktop client with incomplete profile flag: {}", targetUrl);
      response.sendRedirect(targetUrl);
      return;
    }
    // Build desktop redirect URL
    String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
        .queryParam("token", jwt)
        .build().toUriString();

    log.debug("Redirecting desktop client to: {}", targetUrl);
    response.sendRedirect(targetUrl);
  }
}