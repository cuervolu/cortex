package com.cortex.backend.mappers;

import com.cortex.backend.controllers.user.dto.UserResponse;
import com.cortex.backend.entities.user.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

  public UserResponse toUserResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .fullName(user.getFullName())
        .avatar(user.getAvatar())
        .dateOfBirth(user.getDateOfBirth())
        .accountLocked(user.isAccountLocked())
        .enabled(user.isEnabled())
        .build();
  }

}
