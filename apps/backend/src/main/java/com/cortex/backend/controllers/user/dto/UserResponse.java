package com.cortex.backend.controllers.user.dto;

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
public class UserResponse {
  private Long id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String fullName;
  private String avatar;
  private boolean accountLocked;
  private boolean enabled;
}
