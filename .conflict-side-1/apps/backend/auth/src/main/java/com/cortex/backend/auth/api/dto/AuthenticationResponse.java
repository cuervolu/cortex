package com.cortex.backend.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {

  private String token;
  @JsonProperty("refresh_token")
  private String refreshToken;
}
