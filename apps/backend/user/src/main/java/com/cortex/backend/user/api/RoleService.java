package com.cortex.backend.user.api;

import com.cortex.backend.core.domain.Role;
import com.cortex.backend.user.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  @Cacheable("roles")
  public List<Role> getAllRoles() {
    return (List<Role>) roleRepository.findAll();
  }

  @Cacheable(value = "roles", unless = "#result == null")
  public Optional<Role> findByName(String name) {
    return roleRepository.findByName(name);
  }

  @CachePut(value = "roles", key = "#role.name")
  public Role saveRole(Role role) {
    roleRepository.save(role);
    return role;
  }
}