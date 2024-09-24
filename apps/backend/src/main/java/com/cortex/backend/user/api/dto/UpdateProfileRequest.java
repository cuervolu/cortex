package com.cortex.backend.user.api.dto;

import com.cortex.backend.common.types.Gender;
import com.cortex.backend.common.validation.ValueOfEnum;
import com.cortex.backend.user.validations.ValidCountry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class UpdateProfileRequest {

  @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
  private String firstName;

  @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
  private String lastName;

  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @Email(message = "Invalid email format")
  private String email;

  private LocalDate dateOfBirth;

  private MultipartFile avatar;

  @Size(min = 2, max = 3, message = "Country code must be 2 or 3 characters")
  @ValidCountry()
  private String countryCode;

  @ValueOfEnum(enumClass = Gender.class, message = "Invalid Gender type")
  private String gender;
}
