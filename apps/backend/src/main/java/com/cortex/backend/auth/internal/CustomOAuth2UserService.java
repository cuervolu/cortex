package com.cortex.backend.auth.internal;

import com.cortex.backend.common.ImageUtils;
import com.cortex.backend.auth.internal.infrastructure.BaseOAuth2UserService;
import com.cortex.backend.user.domain.User;
import com.cortex.backend.user.repository.RoleRepository;
import com.cortex.backend.user.repository.UserRepository;
import com.cortex.backend.media.api.MediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuth2UserService extends BaseOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();


  public CustomOAuth2UserService(UserRepository userRepository, ImageUtils imageUtils,
      MediaService mediaService, RoleRepository roleRepository)  {
    super(userRepository, imageUtils, mediaService, roleRepository);
  }
  
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = defaultOAuth2UserService.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId();
    String email = extractEmail(oauth2User, provider);
    String externalId = extractExternalId(oauth2User, provider);

    User user = processUser(oauth2User, provider, email, externalId);
    return new OAuth2UserPrincipal(user, oauth2User);
  }

  private String extractEmail(OAuth2User oAuth2User, String provider) {
    if (GOOGLE_PROVIDER.equals(provider) && oAuth2User instanceof OidcUser oidcUser) {
      return oidcUser.getEmail();
    } else {
      return oAuth2User.getAttribute("email");
    }
  }

  private String extractExternalId(OAuth2User oauth2User, String provider) {
    String externalId = null;
    switch (provider) {
      case GOOGLE_PROVIDER:
        externalId = oauth2User.getAttribute("sub");
        break;
      case GITHUB_PROVIDER:
        Object githubId = oauth2User.getAttribute("id");
        if (githubId != null) {
          externalId = githubId.toString();
        }
        break;
      default:
        log.warn("Attempt to use unsupported provider: {}", provider);
        throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
    }
    if (externalId == null || externalId.isEmpty()) {
      log.error("Failed to extract external ID for provider: {}", provider);
      throw new OAuth2AuthenticationException(
          "Unable to extract external ID for provider: " + provider);
    }
    log.debug("Successfully extracted external ID for provider: {}", provider);
    return externalId;
  }
}