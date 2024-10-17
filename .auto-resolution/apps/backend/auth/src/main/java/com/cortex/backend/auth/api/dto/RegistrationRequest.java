package com.cortex.backend.auth.api.dto;

import com.cortex.backend.core.common.types.Gender;
import com.cortex.backend.core.common.validation.ValueOfEnum;
import com.cortex.backend.user.validations.ValidCountry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

  @NotEmpty(message = "First name is required")
  @NotBlank(message = "First name is required")
  private String firstname;

  @NotEmpty(message = "Last name is required")
  @NotBlank(message = "Last name is required")
  private String lastname;

  @NotEmpty(message = "Username is required")
  @NotBlank(message = "Username is required")
  @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
  private String username;

  @NotEmpty(message = "Email is required")
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @NotEmpty(message = "Password is required")
  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @Past(message = "Date of birth must be in the past")
  private LocalDate dateOfBirth;

  @NotBlank(message = "Country code is required")
  @Size(min = 2, max = 3, message = "Country code must be 2 or 3 characters")
//  @ValidCountry()
  private String countryCode;

  @NotNull(message = "Gender is required")
  @ValueOfEnum(enumClass = Gender.class, message = "Invalid Gender type")
  private String gender;
}