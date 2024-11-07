package com.cortex.backend.auth.internal;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class ConditionalOAuth2SuccessHandler implements AuthenticationSuccessHandler {
  private final OAuth2AuthenticationSuccessHandler webHandler;
  private final PKCEAwareOAuth2SuccessHandler desktopHandler;
  private final PKCEAuthorizationRequestRepository pkceAuthorizationRequestRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // Check additional parameters from the authorization request
    OAuth2AuthorizationRequest authRequest =
        pkceAuthorizationRequestRepository.loadAuthorizationRequest(request);

    String clientType = null;
    String redirectUri;

    if (authRequest != null && authRequest.getAdditionalParameters() != null) {
      clientType = (String) authRequest.getAdditionalParameters().get("client_type");
      redirectUri = (String) authRequest.getAdditionalParameters().get("redirect_uri");
      log.debug("Found client_type: {}, redirect_uri: {}", clientType, redirectUri);
    }

    if ("desktop".equals(clientType)) {
      log.info("Handling desktop client OAuth success");
      desktopHandler.onAuthenticationSuccess(request, response, authentication);
    } else {
      log.info("Handling web client OAuth success");
      webHandler.onAuthenticationSuccess(request, response, authentication);
    }
  }
}