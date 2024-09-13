package com.cortex.backend.services;

import com.cortex.backend.controllers.user.dto.ChangePasswordRequest;
import com.cortex.backend.controllers.user.dto.UserResponse;
import com.cortex.backend.entities.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface IUserService {

  Optional<UserResponse> getUserById(Long id);

  Optional<UserResponse> getUserByEmail(String email);

  Optional<UserResponse> getUserByUsername(String username);

  List<UserResponse> getAllUsers();
  
  void changePassword(ChangePasswordRequest request, Authentication connectedUser);

  User updateUser(Long id, User userDetails);

  void deleteUser(Long id);
}
