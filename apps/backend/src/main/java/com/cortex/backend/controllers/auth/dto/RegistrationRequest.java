package com.cortex.backend.controllers.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
  @NotEmpty(message = "First name is required")
  @NotBlank(message = "First name is required")
  private String firstname;

  @NotEmpty(message = "Last name is required")
  @NotBlank(message = "Last name is required")
  private String lastname;

  @NotEmpty(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
  private String username;

  @NotEmpty(message = "Email is required")
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @NotEmpty(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;
}
