package com.cortex.backend.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationRequest {

  @NotEmpty(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
  private String username;

  @NotEmpty(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;
}
