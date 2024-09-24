package com.cortex.backend.auth.api;

import com.cortex.backend.auth.api.dto.AuthenticationRequest;
import com.cortex.backend.auth.api.dto.AuthenticationResponse;
import com.cortex.backend.auth.api.dto.RegistrationRequest;

public interface AuthenticationService {
  AuthenticationResponse authenticate(AuthenticationRequest request);
  void register(RegistrationRequest request);
  void activateAccount(String token);
}