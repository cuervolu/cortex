package com.cortex.backend.infrastructure.security;

import com.cortex.backend.common.ImageUtils;
import com.cortex.backend.user.domain.User;
import com.cortex.backend.infrastructure.persistence.RoleRepository;
import com.cortex.backend.infrastructure.persistence.UserRepository;
import com.cortex.backend.media.api.MediaService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends BaseOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

  private final OidcUserService defaultOidcUserService = new OidcUserService();

  public CustomOidcUserService(UserRepository userRepository, ImageUtils imageUtils,
      MediaService mediaService, RoleRepository roleRepository)  {
    super(userRepository, imageUtils, mediaService, roleRepository);
  }

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser oidcUser = defaultOidcUserService.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId();
    String email = oidcUser.getEmail();
    String externalId = oidcUser.getSubject();

    User user = processUser(oidcUser, provider, email, externalId);
    return new OidcUserPrincipal(user, oidcUser);
  }
}