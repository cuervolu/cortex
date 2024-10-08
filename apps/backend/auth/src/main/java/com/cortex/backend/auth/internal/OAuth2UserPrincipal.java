package com.cortex.backend.auth.internal;

import com.cortex.backend.core.domain.User;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public record OAuth2UserPrincipal(User user, OAuth2User oauth2User) implements OAuth2User {

  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getAuthorities();
  }

  @Override
  public String getName() {
    return user.getUsername();
  }
}