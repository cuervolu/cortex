package com.cortex.backend.auth.internal;

import com.cortex.backend.auth.api.AuthenticationService;
import com.cortex.backend.auth.api.dto.AuthenticationRequest;
import com.cortex.backend.auth.api.dto.AuthenticationResponse;
import com.cortex.backend.auth.api.dto.RefreshTokenRequest;
import com.cortex.backend.auth.api.dto.RegistrationRequest;
import com.cortex.backend.core.common.email.EmailService;
import com.cortex.backend.core.common.email.EmailTemplateName;
import com.cortex.backend.core.common.exception.InvalidTokenException;
import com.cortex.backend.core.common.types.Gender;
import com.cortex.backend.core.domain.Role;
import com.cortex.backend.core.domain.Token;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.user.repository.RoleRepository;
import com.cortex.backend.user.repository.TokenRepository;
import com.cortex.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtServiceImpl jwtService;
  private final EmailService emailService;
  private final Environment env;

  @Value("${application.mailing.frontend.activation-url}")
  private String activationUrl;

  @Transactional
  public void register(RegistrationRequest request) {
    List<Role> roles = new ArrayList<>();

    boolean isFirstUser = userRepository.count() == 0;

    roles.add(roleRepository.findByName("USER")
        .orElseThrow(() -> new IllegalStateException("USER role not found")));

    if (isFirstUser) {
      roles.add(roleRepository.findByName("ADMIN")
          .orElseThrow(() -> new IllegalStateException("ADMIN role not found")));
    }

    var user = User.builder()
        .firstName(request.getFirstname())
        .lastName(request.getLastname())
        .username(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .accountLocked(false)
        .enabled(false)
        .roles(roles)
        .dateOfBirth(request.getDateOfBirth())
        .countryCode(request.getCountryCode())
        .gender(Gender.valueOf(request.getGender()))
        .build();

    userRepository.save(user);
    if (!isDevProfile()) {
      sendValidationEmail(user);
    } else {
      // In dev mode, automatically activate the account
      log.info("Dev profile detected, activating account automatically");
      user.setEnabled(true);
      userRepository.save(user);
    }
  }

  private String generateAndSaveActivationToken(User user) {
    String generatedToken = generateActivationCode();
    var token =
        Token.builder()
            .token(generatedToken)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .user(user)
            .build();
    tokenRepository.save(token);
    return generatedToken;
  }

  private String generateActivationCode() {
    String characters = "0123456789";
    StringBuilder codeBuilder = new StringBuilder();
    SecureRandom secureRandom = new SecureRandom();
    for (int i = 0; i < 6; i++) {
      int randomIndex = secureRandom.nextInt(characters.length());
      codeBuilder.append(characters.charAt(randomIndex));
    }
    return codeBuilder.toString();
  }

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
    var claims = new HashMap<String, Object>();
    var user = ((User) auth.getPrincipal());
    user.updateLoginStats();
    userRepository.save(user);
    claims.put("fullname", user.getFullName());
    var jwtToken = jwtService.generateToken(claims, user);
    var refreshToken = jwtService.generateRefreshToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public void activateAccount(String token) {
    Token savedToken =
        tokenRepository
            .findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token not found"));

    if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
      sendValidationEmail(savedToken.getUser());
      throw new InvalidTokenException("Token expired, new token sent to email");
    }
    var user =
        userRepository
            .findById(savedToken.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    user.setEnabled(true);
    userRepository.save(user);
    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
  }

  @Override
  public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();
    String username = jwtService.extractUsername(refreshToken);
    if (username != null) {
      User user = this.userRepository.findByUsername(username)
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      if (jwtService.isTokenValid(refreshToken, user)) {
        var claims = new HashMap<String, Object>();
        claims.put("fullname", user.getFullName());
        String accessToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder()
            .token(accessToken)
            .refreshToken(refreshToken)
            .build();
      }
    }
    throw new InvalidTokenException("Invalid refresh token");
  }

  private void sendValidationEmail(User user) {
    var newToken = generateAndSaveActivationToken(user);

    Map<String, Object> templateVariables = new HashMap<>();
    templateVariables.put("username", user.getUsername());
    templateVariables.put("activationUrl", activationUrl + "?token=" + newToken);
    templateVariables.put("activationCode", newToken);

    emailService.sendEmail(
        user.getEmail(),
        EmailTemplateName.ACTIVATE_ACCOUNT,
        templateVariables,
        "Activate your Cortex account and start your coding adventure today! Click the button or use your token to get started."
    );
  }

  private boolean isDevProfile() {
    return env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev");
  }
}
