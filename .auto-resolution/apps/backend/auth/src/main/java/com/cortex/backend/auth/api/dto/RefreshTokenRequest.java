package com.cortex.backend.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  
@AllArgsConstructor 
@Builder
public class RefreshTokenRequest {
  
  @NotNull(message = "Refresh Token is required")
  @NotBlank(message = "Refresh Token cannot be blank")
  @JsonProperty("refresh_token")
  private String refreshToken;
}
