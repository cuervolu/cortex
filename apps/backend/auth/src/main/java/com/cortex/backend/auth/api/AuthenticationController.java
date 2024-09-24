package com.cortex.backend.auth.api;

import com.cortex.backend.auth.api.dto.AuthenticationRequest;
import com.cortex.backend.auth.api.dto.AuthenticationResponse;
import com.cortex.backend.auth.api.dto.PasswordResetResponse;
import com.cortex.backend.auth.api.dto.RegistrationRequest;
import com.cortex.backend.user.api.UserService;
import com.cortex.backend.user.api.dto.ForgotPasswordRequest;
import com.cortex.backend.user.api.dto.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and account management")
public class AuthenticationController {

  private final AuthenticationService service;
  private final UserService userService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Register a new user", description = "Creates a new user account and sends an activation email")
  @ApiResponse(responseCode = "202", description = "User registered successfully")
  public ResponseEntity<Void> register(@RequestBody @Valid RegistrationRequest request) {
    service.register(request);
    return ResponseEntity.accepted().build();
  }

  @PostMapping("/authenticate")
  @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
  @ApiResponse(responseCode = "200", description = "Authentication successful",
      content = @Content(schema = @Schema(implementation = AuthenticationResponse.class)))
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody @Valid AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @GetMapping("/activate-account")
  @Operation(summary = "Activate user account", description = "Activates a user account using the provided token")
  @ApiResponse(responseCode = "200", description = "Account activated successfully")
  @ApiResponse(responseCode = "400", description = "Invalid or expired token")
  public void confirm(@Parameter(description = "Activation token") @RequestParam String token) {
    service.activateAccount(token);
  }

  @PostMapping("/forgot-password")
  @Operation(summary = "Initiate password reset",
      description = "Sends a password reset email to the user. The reset token is valid for 1 hour.")
  @ApiResponse(responseCode = "200", description = "Password reset email sent",
      content = @Content(schema = @Schema(implementation = PasswordResetResponse.class)))
  public ResponseEntity<PasswordResetResponse> forgotPassword(
      @RequestBody @Valid ForgotPasswordRequest request) {
    userService.initiatePasswordReset(request.email());
    return ResponseEntity.ok(new PasswordResetResponse("Password reset email sent successfully"));
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Reset password",
      description = "Resets the user's password using the provided token. The token must be used within 1 hour of issuance.")
  @ApiResponse(responseCode = "200", description = "Password reset successful",
      content = @Content(schema = @Schema(implementation = PasswordResetResponse.class)))
  @ApiResponse(responseCode = "400", description = "Invalid or expired token")
  public ResponseEntity<PasswordResetResponse> resetPassword(
      @RequestBody @Valid ResetPasswordRequest request) {
    userService.resetPassword(request.token(), request.newPassword());
    return ResponseEntity.ok(new PasswordResetResponse("Password reset successfully"));
  }
}