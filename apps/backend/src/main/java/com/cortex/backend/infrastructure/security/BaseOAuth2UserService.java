package com.cortex.backend.infrastructure.security;

import com.cortex.backend.common.ImageUtils;
import com.cortex.backend.auth.api.dto.RegistrationRequest;
import com.cortex.backend.media.domain.Media;
import com.cortex.backend.user.domain.Role;
import com.cortex.backend.user.domain.User;
import com.cortex.backend.common.exception.FileSizeExceededException;
import com.cortex.backend.common.exception.InvalidURIException;
import com.cortex.backend.infrastructure.persistence.RoleRepository;
import com.cortex.backend.infrastructure.persistence.UserRepository;
import com.cortex.backend.media.api.MediaService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public abstract class BaseOAuth2UserService {

  private final UserRepository userRepository;
  private final ImageUtils imageUtils;
  private final MediaService mediaService;
  private final RoleRepository roleRepository;

  protected BaseOAuth2UserService(UserRepository userRepository, ImageUtils imageUtils,
      MediaService mediaService, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.imageUtils = imageUtils;
    this.mediaService = mediaService;
    this.roleRepository = roleRepository;
  }

  // Constants
  protected static final String GITHUB_PROVIDER = "github";
  protected static final String GOOGLE_PROVIDER = "google";
  protected static final String FAMILY_NAME = "family_name";
  protected static final String GIVEN_NAME = "given_name";
  protected static final String PICTURE = "picture";
  protected static final String AVATAR_URL = "avatar_url";
  protected static final String BIRTHDATE = "birthdate";


  protected User processUser(OAuth2User oauth2User, String provider, String email,
      String externalId) {
    return userRepository.findByEmail(email)
        .map(existingUser -> updateExistingUser(existingUser, oauth2User, provider, externalId))
        .orElseGet(() -> createNewUser(oauth2User, provider, externalId, email));
  }

  private User updateExistingUser(User user, OAuth2User oauth2User, String provider,
      String externalId) {
    user.getProviders().add(provider);
    updateUserDetails(user, oauth2User, provider, externalId);
    return userRepository.save(user);
  }

  private User createNewUser(OAuth2User oauth2User, String provider, String externalId,
      String email) {
    List<Role> roles = new ArrayList<>();
    RegistrationRequest request = RegistrationRequest.builder()
        .email(email)
        .username(generateUsername(oauth2User, provider))
        .firstname(oauth2User.getAttribute(GIVEN_NAME))
        .lastname(oauth2User.getAttribute(FAMILY_NAME))
        .dateOfBirth(extractDateOfBirth(oauth2User, provider))
        .build();

    // Check if this is the first user
    boolean isFirstUser = userRepository.count() == 0;

    // Always add USER role
    roles.add(roleRepository.findByName("USER")
        .orElseThrow(() -> new IllegalStateException("USER role not found")));

    // If it's the first user, also add ADMIN role
    if (isFirstUser) {
      roles.add(roleRepository.findByName("ADMIN")
          .orElseThrow(() -> new IllegalStateException("ADMIN role not found")));
    }

    var user = User.builder()
        .firstName(request.getFirstname())
        .lastName(request.getLastname())
        .username(request.getUsername())
        .email(request.getEmail())
        .accountLocked(false)
        .enabled(true) // Enable user by default
        .roles(roles)
        .externalId(externalId)
        .providers(new HashSet<>(Collections.singletonList(provider)))
        .build();
    // Save the user first to get an ID
    user = userRepository.save(user);

    // Now update user details including avatar
    updateUserDetails(user, oauth2User, provider, externalId);

    // Save the user again with updated details
    return userRepository.save(user);
  }

  private void updateUserDetails(User user, OAuth2User oauth2User, String provider,
      String externalId) {
    if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
      user.setFirstName(oauth2User.getAttribute(GIVEN_NAME));
    }
    if (user.getLastName() == null || user.getLastName().isEmpty()) {
      user.setLastName(oauth2User.getAttribute(FAMILY_NAME));
    }
    if (user.getDateOfBirth() == null) {
      user.setDateOfBirth(extractDateOfBirth(oauth2User, provider));
    }
    if (user.getExternalId() == null) {
      user.setExternalId(externalId);
    }

    // Solo actualizamos el avatar si el usuario no tiene uno
    if (user.getAvatar() == null) {
      updateUserAvatar(user, oauth2User, provider);
    }
  }

  private void updateUserAvatar(User user, OAuth2User oauth2User, String provider) {
    String avatarUrl = extractAvatarUrl(oauth2User, provider);
    if (avatarUrl != null) {
      try {
        MultipartFile avatarFile = ImageUtils.downloadImageFromUrl(avatarUrl);
        imageUtils.validateFileSize(avatarFile);
        if (imageUtils.isValidImageFile(avatarFile)) {
          Media avatarMedia = mediaService.uploadMedia(avatarFile, "User avatar for OAuth user",
              "avatar", user.getId().toString());
          user.setAvatar(avatarMedia);
        } else {
          log.warn("Invalid image file type for OAuth user avatar");
        }
      } catch (IOException | FileSizeExceededException e) {
        log.error("Error processing avatar for OAuth user", e);
      } catch (URISyntaxException e) {
        log.error("Invalid URI for avatar URL", e);
        throw new InvalidURIException("Invalid URI for avatar URL");
      }
    }
  }

  private String generateUsername(OAuth2User oauth2User, String provider) {
    String username;
    switch (provider) {
      case GITHUB_PROVIDER:
        username = oauth2User.getAttribute("login");
        break;
      case GOOGLE_PROVIDER:
        String firstName = oauth2User.getAttribute(GIVEN_NAME);
        String lastName = oauth2User.getAttribute(FAMILY_NAME);
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
      case GITHUB_PROVIDER -> oauth2User.getAttribute(AVATAR_URL);
      case GOOGLE_PROVIDER -> oauth2User.getAttribute(PICTURE);
      default -> null;
    };
  }

  private LocalDate extractDateOfBirth(OAuth2User oauth2User, String provider) {
    // Note: GitHub doesn't provide date of birth
    if (GOOGLE_PROVIDER.equals(provider)) {
      String birthdate = oauth2User.getAttribute(BIRTHDATE);
      if (birthdate != null) {
        return LocalDate.parse(birthdate);
      }
    }
    return null;
  }


}