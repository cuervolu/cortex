package com.cortex.backend.security;

import com.cortex.backend.entities.user.Role;
import com.cortex.backend.entities.user.User;
import com.cortex.backend.repositories.RoleRepository;
import com.cortex.backend.repositories.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class BaseOAuth2UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  protected static final String GITHUB_PROVIDER = "github";
  protected static final String GOOGLE_PROVIDER = "google";

  protected User processUser(OAuth2User oauth2User, String provider, String email, String externalId) {
    return userRepository.findByEmail(email)
        .map(existingUser -> updateExistingUser(existingUser, oauth2User, provider, externalId))
        .orElseGet(() -> createNewUser(oauth2User, provider, externalId, email));
  }

  private User updateExistingUser(User user, OAuth2User oauth2User, String provider, String externalId) {
    user.getProviders().add(provider);
    updateUserDetails(user, oauth2User, provider, externalId);
    return userRepository.save(user);
  }

  private User createNewUser(OAuth2User oauth2User, String provider, String externalId, String email) {
    User user = new User();
    user.setEmail(email);
    user.setUsername(generateUsername(oauth2User, provider));
    user.setEnabled(true);
    user.setProviders(new HashSet<>(Collections.singletonList(provider)));
    updateUserDetails(user, oauth2User, provider, externalId);

    Role userRole = roleRepository.findByName("USER")
        .orElseThrow(() -> new RuntimeException("Default role not found"));
    user.setRoles(List.of(userRole));
    
    return userRepository.save(user);
  }

  private void updateUserDetails(User user, OAuth2User oauth2User, String provider, String externalId) {
    user.setExternalId(externalId);
    user.setFirstName(oauth2User.getAttribute("given_name"));
    user.setLastName(oauth2User.getAttribute("family_name"));
    user.setAvatar(extractAvatarUrl(oauth2User, provider));
    user.setDateOfBirth(extractDateOfBirth(oauth2User, provider));
  }

  private String generateUsername(OAuth2User oauth2User, String provider) {
    String username;
    switch (provider) {
      case GITHUB_PROVIDER:
        username = oauth2User.getAttribute("login");
        break;
      case GOOGLE_PROVIDER:
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        username = (firstName + "." + lastName).toLowerCase();
        break;
      default:
        throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
    }
    // Ensure username is unique
    String baseUsername = username;
    int suffix = 1;
    while (userRepository.findByUsername(username).isPresent()) {
      username = baseUsername + suffix;
      suffix++;
    }
    return username;
  }

  private String extractAvatarUrl(OAuth2User oauth2User, String provider) {
    return switch (provider) {
      case GITHUB_PROVIDER -> oauth2User.getAttribute("avatar_url");
      case GOOGLE_PROVIDER -> oauth2User.getAttribute("picture");
      default -> null;
    };
  }

  private LocalDate extractDateOfBirth(OAuth2User oauth2User, String provider) {
    // Note: GitHub doesn't provide date of birth
    if (GOOGLE_PROVIDER.equals(provider)) {
      String birthdate = oauth2User.getAttribute("birthdate");
      if (birthdate != null) {
        return LocalDate.parse(birthdate);
      }
    }
    return null;
  }

  
}