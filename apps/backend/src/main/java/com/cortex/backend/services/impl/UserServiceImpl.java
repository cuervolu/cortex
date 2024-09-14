package com.cortex.backend.services.impl;

import com.cortex.backend.controllers.user.dto.ChangePasswordRequest;
import com.cortex.backend.controllers.user.dto.UpdateProfileRequest;
import com.cortex.backend.controllers.user.dto.UserResponse;
import com.cortex.backend.entities.user.User;
import com.cortex.backend.exception.IncorrectCurrentPasswordException;
import com.cortex.backend.exception.NewPasswordDoesNotMatchException;
import com.cortex.backend.mappers.UserMapper;
import com.cortex.backend.repositories.UserRepository;
import com.cortex.backend.services.CloudinaryService;
import com.cortex.backend.services.IUserService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CloudinaryService cloudinaryService;

  @Override
  public Optional<UserResponse> getUserById(Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(userMapper::toUserResponse);
  }


  @Override
  public Optional<UserResponse> getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.map(userMapper::toUserResponse);
  }

  @Override
  public Optional<UserResponse> getUserByUsername(String username) {
    Optional<User> user = userRepository.findByUsername(username);
    return user.map(userMapper::toUserResponse);
  }

  @Override
  public List<UserResponse> getAllUsers() {
    List<User> users = (List<User>) userRepository.findAll();
    return users.stream().map(userMapper::toUserResponse).toList();
  }

  @Override
  @Transactional
  public void changePassword(ChangePasswordRequest request, Authentication connectedUser) {
    var user = (User) connectedUser.getPrincipal();

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new IncorrectCurrentPasswordException("Current password is incorrect");
    }

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new NewPasswordDoesNotMatchException("New password and confirm password do not match");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

  }

  @Transactional
  @Override
  public UserResponse updateProfile(Long userId, UpdateProfileRequest request) throws IOException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    if (request.getUsername() != null) {
      user.setUsername(request.getUsername());
    }
    if (request.getEmail() != null) {
      user.setEmail(request.getEmail());
    }
    if (request.getDateOfBirth() != null) {
      user.setDateOfBirth(request.getDateOfBirth());
    }

    if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
      String avatarUrl = uploadAvatar(request.getAvatar(), userId);
      user.setAvatar(avatarUrl);
    }

    User updatedUser = userRepository.save(user);
    return userMapper.toUserResponse(updatedUser);
  }

  private String uploadAvatar(MultipartFile file, Long userId) throws IOException {
    var uploadResult = cloudinaryService.uploadImage(file, "avatars/" + userId);
    return cloudinaryService.createUrl((String) uploadResult.get("public_id"), 200, 200);
  }

  @Override
  public void deleteUser(Long id) {
    //TODO: Implement this method
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
