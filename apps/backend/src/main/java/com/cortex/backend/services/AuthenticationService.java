package com.cortex.backend.services;

import com.cortex.backend.config.EmailTemplateName;
import com.cortex.backend.controllers.auth.dto.AuthenticationRequest;
import com.cortex.backend.controllers.auth.dto.AuthenticationResponse;
import com.cortex.backend.controllers.auth.dto.RegistrationRequest;
import com.cortex.backend.entities.user.Token;
import com.cortex.backend.entities.user.User;
import com.cortex.backend.exception.InvalidTokenException;
import com.cortex.backend.repositories.RoleRepository;
import com.cortex.backend.repositories.TokenRepository;
import com.cortex.backend.repositories.UserRepository;
import com.cortex.backend.security.JwtService;
import com.resend.core.exception.ResendException;
import java.util.HashMap;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final EmailService emailService;

  @Value("${application.mailing.frontend.activation-url}")
  private String activationUrl;
  
  public void register(RegistrationRequest request) throws ResendException {
    var userRole =
        roleRepository
            .findByName("USER")
            .orElseThrow(() -> new IllegalStateException("Role not found"));
    var user =
        User.builder()
            .firstName(request.getFirstname())
            .lastName(request.getLastname())
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .accountLocked(false)
            .enabled(false)
            .roles(List.of(userRole))
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

  //  @Transactional
  public void activateAccount(String token) throws ResendException {
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

  private void sendValidationEmail(User user) throws ResendException {
    var newToken = generateAndSaveActivationToken(user);

    Map<String, Object> templateVariables = new HashMap<>();
    templateVariables.put("username", user.getUsername());
    templateVariables.put("activationUrl", activationUrl);
    templateVariables.put("activationCode", newToken);

    emailService.sendEmail(
        user.getEmail(),
        EmailTemplateName.ACTIVATE_ACCOUNT,
        templateVariables,
        "Account Activation"
    );
  }



}
