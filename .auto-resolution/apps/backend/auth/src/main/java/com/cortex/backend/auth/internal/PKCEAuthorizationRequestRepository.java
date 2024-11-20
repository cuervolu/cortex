package com.cortex.backend.auth.internal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Slf4j
public class PKCEAuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
  private static final String CLIENT_TYPE_PARAM = "client_type";
  private static final String REDIRECT_URI_PARAM = "redirect_uri";
  private static final int COOKIE_EXPIRE_SECONDS = 180;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    OAuth2AuthorizationRequest authRequest = CookieUtils.deserialize(request,
        OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, OAuth2AuthorizationRequest.class);

    if (authRequest != null) {
      Map<String, Object> additionalParams = new HashMap<>(
          authRequest.getAdditionalParameters() != null ? authRequest.getAdditionalParameters()
              : new HashMap<>());

      // Restore the client_type and redirect_uri from request parameters if present
      String clientType = request.getParameter(CLIENT_TYPE_PARAM);
      String redirectUri = request.getParameter(REDIRECT_URI_PARAM);

      if (clientType != null) {
        additionalParams.put(CLIENT_TYPE_PARAM, clientType);
        log.debug("Restored client_type from request: {}", clientType);
      }

      if (redirectUri != null) {
        additionalParams.put(REDIRECT_URI_PARAM, redirectUri);
        log.debug("Restored redirect_uri from request: {}", redirectUri);
      }

      // If we have additional parameters, create a new request with them
      if (!additionalParams.equals(authRequest.getAdditionalParameters())) {
        authRequest = OAuth2AuthorizationRequest.from(authRequest)
            .additionalParameters(additionalParams)
            .build();
      }
    }

    return authRequest;
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request, HttpServletResponse response) {
    if (authorizationRequest == null) {
      this.removeAuthorizationRequest(request, response);
      return;
    }

    // Get initial parameters from request
    String clientType = request.getParameter(CLIENT_TYPE_PARAM);
    String redirectUri = request.getParameter(REDIRECT_URI_PARAM);

    // Create additional parameters map if needed
    Map<String, Object> additionalParams = new HashMap<>(
        authorizationRequest.getAdditionalParameters() != null ?
            authorizationRequest.getAdditionalParameters() : new HashMap<>()
    );

    // Add our parameters
    if (clientType != null) {
      additionalParams.put(CLIENT_TYPE_PARAM, clientType);
      log.debug("Saving client_type to auth request: {}", clientType);
    }
    if (redirectUri != null) {
      additionalParams.put(REDIRECT_URI_PARAM, redirectUri);
      log.debug("Saving redirect_uri to auth request: {}", redirectUri);
    }

    // Create new authorization request with additional parameters
    OAuth2AuthorizationRequest modifiedRequest = OAuth2AuthorizationRequest
        .from(authorizationRequest)
        .additionalParameters(additionalParams)
        .build();

    CookieUtils.serialize(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, modifiedRequest, response,
        COOKIE_EXPIRE_SECONDS);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    return loadAuthorizationRequest(request);
  }
}