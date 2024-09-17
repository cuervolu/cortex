package com.cortex.backend.controllers.user.dto;

import com.cortex.backend.entities.user.Gender;
import java.time.LocalDate;
import java.util.List;
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
  private String avatarUrl;
  private LocalDate dateOfBirth;
  private String countryCode;
  private Gender gender;
  private boolean accountLocked;
  private boolean enabled;
  private List<String> roles;
}
