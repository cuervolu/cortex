package com.cortex.backend.user.internal;

import com.cortex.backend.core.domain.Media;
import com.cortex.backend.media.api.ImageUtils;
import com.cortex.backend.core.common.email.EmailService;
import com.cortex.backend.core.common.email.EmailTemplateName;
import com.cortex.backend.core.common.exception.IncorrectCurrentPasswordException;
import com.cortex.backend.core.common.exception.InvalidFileTypeException;
import com.cortex.backend.core.common.exception.InvalidTokenException;
import com.cortex.backend.core.common.exception.NewPasswordDoesNotMatchException;
import com.cortex.backend.core.common.types.Gender;
import com.cortex.backend.core.common.types.TokenType;
import com.cortex.backend.media.api.MediaService;
import com.cortex.backend.user.api.UserService;
import com.cortex.backend.user.api.dto.ChangePasswordRequest;
import com.cortex.backend.user.api.dto.UpdateProfileRequest;
import com.cortex.backend.user.api.dto.UserResponse;
import com.cortex.backend.core.domain.Role;
import com.cortex.backend.core.domain.Token;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.user.repository.RoleRepository;
import com.cortex.backend.user.repository.TokenRepository;
import com.cortex.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  
  private static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";
  private static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: ";

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final MediaService mediaService;
  private final ImageUtils imageUtils;

  @Value("${application.frontend.password-reset-url}")
  private String passwordResetUrl;

  @Override
  public Optional<UserResponse> getUserById(Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(userMapper::toUserResponse);
  }


  @Override
  public Optional<UserResponse> getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.map(userMapper::toUserResponse);
  }

  @Override
  public Optional<UserResponse> getUserByUsername(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    return user.map(userMapper::toUserResponse);
  }

  @Override
  public List<UserResponse> getAllUsers() {
    List<User> users = (List<User>) userRepository.findAll();
    return users.stream().map(userMapper::toUserResponse).toList();
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordRequest request, Authentication connectedUser) {
    var user = (User) connectedUser.getPrincipal();

    // If the user has no password set, we don't need to check the current password
    if (user.getPassword() != null && !user.getPassword().isEmpty() && !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
        throw new IncorrectCurrentPasswordException("Current password is incorrect");
      }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new NewPasswordDoesNotMatchException("New password and confirm password do not match");
    }

    
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void initiatePasswordReset(String email)
      throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_EMAIL + email));

    String tokenValue = generateUniqueToken();
    LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

    Token resetToken = Token.builder()
        .token(tokenValue)
        .tokenType(TokenType.PASSWORD_RESET)
        .user(user)
        .createdAt(LocalDateTime.now())
        .expiresAt(expiryDate)
        .build();

    tokenRepository.save(resetToken);

    String resetUrl = passwordResetUrl + "?token=" + tokenValue;
    Map<String, Object> templateVariables = new HashMap<>();
    templateVariables.put("username", user.getUsername());
    templateVariables.put("resetUrl", resetUrl);

    emailService.sendEmail(
        user.getEmail(),
        EmailTemplateName.RESET_PASSWORD,
        templateVariables,
        "Reset Your Cortex Password!"
    );
  }

  @Override
  @Transactional
  public void resetPassword(String tokenValue, String newPassword) {
    Token resetToken = tokenRepository.findByTokenAndTokenType(tokenValue, TokenType.PASSWORD_RESET)
        .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset token"));

    if (resetToken.isExpired()) {
      tokenRepository.delete(resetToken);
      throw new InvalidTokenException("Password reset token has expired");
    }

    User user = resetToken.getUser();
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    resetToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(resetToken);
  }

  private String generateUniqueToken() {
    return UUID.randomUUID().toString();
  }

  @Transactional
  @Override
  public UserResponse updateProfile(Long userId, UpdateProfileRequest request) throws IOException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException(
            USER_NOT_FOUND_WITH_ID + userId
        ));

    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    if (request.getUsername() != null) {
      user.setUsername(request.getUsername());
    }
    if (request.getEmail() != null) {
      user.setEmail(request.getEmail());
    }
    if (request.getDateOfBirth() != null) {
      user.setDateOfBirth(request.getDateOfBirth());
    }

    if (request.getGender() != null) {
      user.setGender(Gender.valueOf(request.getGender()));
    }

    if (request.getCountryCode() != null) {
      user.setCountryCode(request.getCountryCode());
    }

    if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
      imageUtils.validateFileSize(request.getAvatar());
      if (!imageUtils.isValidImageFile(request.getAvatar())) {
        throw new InvalidFileTypeException(
            "Invalid file type. Only JPEG, PNG, and WebP are allowed.");
      }
      Media avatarMedia = uploadAvatar(request.getAvatar(), userId);
      user.setAvatar(avatarMedia);
    }

    User updatedUser = userRepository.save(user);
    return userMapper.toUserResponse(updatedUser);
  }

  private Media uploadAvatar(MultipartFile file, Long userId) throws IOException {
    return mediaService.uploadMedia(file, "User avatar", "avatar", userId.toString());
  }

  @Override
  public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_ID + id));

    user.setEnabled(false);

    tokenRepository.findAllValidTokenByUser(id).forEach(token -> {
      token.setExpiresAt(LocalDateTime.now());
      tokenRepository.save(token);
    });
    
    userRepository.save(user);

    log.info("User with id {} has been disabled", id);
  }

  @Override
  @Transactional
  public UserResponse reEnableUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_ID + id));

    if (user.isEnabled()) {
      throw new IllegalStateException("User is already enabled");
    }

    // Re-enable the user
    user.setEnabled(true);

    User reEnabledUser = userRepository.save(user);
    log.info("User with id {} has been re-enabled", id);

    return userMapper.toUserResponse(reEnabledUser);
  }

  @Override
  @Transactional
  public UserResponse updateUserRoles(Long userId, List<String> roleNames) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_ID + userId));

    List<Role> roles = roleRepository.findAllByNameIn(roleNames);
    if (roles.size() != roleNames.size()) {
      throw new IllegalArgumentException("One or more role names are invalid");
    }

    user.setRoles(roles);
    User updatedUser = userRepository.save(user);

    log.info("Updated roles for user with id {}: {}", userId, roleNames);

    return userMapper.toUserResponse(updatedUser);
  }
}
