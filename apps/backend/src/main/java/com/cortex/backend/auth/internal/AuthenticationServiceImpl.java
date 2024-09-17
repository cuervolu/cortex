package com.cortex.backend.auth.internal;
import com.cortex.backend.auth.api.AuthenticationService;
import com.cortex.backend.auth.api.dto.AuthenticationRequest;
import com.cortex.backend.auth.api.dto.AuthenticationResponse;
import com.cortex.backend.auth.api.dto.RegistrationRequest;
import com.cortex.backend.auth.domain.Token;
import com.cortex.backend.common.exception.InvalidTokenException;
import com.cortex.backend.user.domain.Role;
import com.cortex.backend.user.domain.User;
import com.cortex.backend.infrastructure.config.EmailTemplateName;
import com.cortex.backend.infrastructure.api.JwtService;
import com.cortex.backend.infrastructure.persistence.RoleRepository;
import com.cortex.backend.infrastructure.persistence.TokenRepository;
import com.cortex.backend.infrastructure.persistence.UserRepository;
import com.cortex.backend.infrastructure.api.EmailService;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final EmailService emailService;

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
        .gender(request.getGender())
        .build();

    userRepository.save(user);
    sendValidationEmail(user);
  }

  private String generateAndSaveActivationToken(User user) {
    String generatedToken = generateActivationCode(6);
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

  private String generateActivationCode(int length) {
    String characters = "0123456789";
    StringBuilder codeBuilder = new StringBuilder();
    SecureRandom secureRandom = new SecureRandom();
    for (int i = 0; i < length; i++) {
      int randomIndex = secureRandom.nextInt(characters.length());
      codeBuilder.append(characters.charAt(randomIndex));
    }
    return codeBuilder.toString();
  }

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    var claims = new HashMap<String, Object>();
    var user = ((User) auth.getPrincipal());
    claims.put("fullname", user.getFullName());
    var jwtToken = jwtService.generateToken(claims, user);
    return AuthenticationResponse.builder().token(jwtToken).build();
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
}
