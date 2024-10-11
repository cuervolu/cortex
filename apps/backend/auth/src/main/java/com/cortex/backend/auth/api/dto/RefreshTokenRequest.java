package com.cortex.backend.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  @JsonProperty("refresh_token")
  private String refreshToken;
}
