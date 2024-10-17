package com.cortex.backend.user.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {

  @NotEmpty(message = "Current password is required")
  @NotNull(message = "Current password is required")
  private String currentPassword;
  
  @NotEmpty(message = "New password is required")
  @NotNull(message = "New password is required")
  private String newPassword;
  
  @NotEmpty(message = "Confirm password is required")
  @NotNull(message = "Confirm password is required")
  private String confirmPassword;
}
