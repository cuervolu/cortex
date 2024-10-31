package com.cortex.backend.user.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {
  
  @JsonProperty("current_password")
  private String currentPassword;
  
  @NotEmpty(message = "New password is required")
  @NotNull(message = "New password is required")
  @JsonProperty("new_password")
  private String newPassword;
  
  @NotEmpty(message = "Confirm password is required")
  @NotNull(message = "Confirm password is required")
  @JsonProperty("confirm_password")
  private String confirmPassword;
}
