package com.cortex.backend.services;

import com.cortex.backend.controllers.user.dto.ChangePasswordRequest;
import com.cortex.backend.controllers.user.dto.UpdateProfileRequest;
import com.cortex.backend.controllers.user.dto.UserResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IUserService {

  Optional<UserResponse> getUserById(Long id);

  Optional<UserResponse> getUserByEmail(String email);

  Optional<UserResponse> getUserByUsername(String username);

  List<UserResponse> getAllUsers();

  void changePassword(ChangePasswordRequest request, Authentication connectedUser);

  void initiatePasswordReset(String email) throws UsernameNotFoundException;

  void resetPassword(String tokenValue, String newPassword);
  
  
  UserResponse updateProfile(Long userId, UpdateProfileRequest request) throws IOException;

  void deleteUser(Long id);

  UserResponse reEnableUser(Long id);

  UserResponse updateUserRoles(Long userId, List<String> roleNames);
}
