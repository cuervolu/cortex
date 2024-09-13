package com.cortex.backend.services.impl;

import com.cortex.backend.controllers.user.dto.UserResponse;
import com.cortex.backend.entities.user.User;
import com.cortex.backend.mappers.UserMapper;
import com.cortex.backend.repositories.UserRepository;
import com.cortex.backend.security.JwtService;
import com.cortex.backend.services.IUserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;

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
  public User updateUser(Long id, User userDetails) {
    //TODO: Implement this method
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void deleteUser(Long id) {
    //TODO: Implement this method
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
