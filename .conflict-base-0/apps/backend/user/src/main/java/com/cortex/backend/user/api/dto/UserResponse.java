package com.cortex.backend.user.api.dto;

import com.cortex.backend.core.common.types.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("avatar_url")
  private String avatarUrl;

  @JsonProperty("date_of_birth")
  private LocalDate dateOfBirth;

  @JsonProperty("country_code")
  private String countryCode;
  private Gender gender;

  @JsonProperty("account_locked")
  private boolean accountLocked;
  private boolean enabled;
  private List<String> roles;

  @JsonProperty("created_at")
  private LocalDate createdAt;

  @JsonProperty("updated_at")
  private LocalDate updatedAt;
  
  @JsonProperty("has_password")
  private boolean hasPassword;
}
