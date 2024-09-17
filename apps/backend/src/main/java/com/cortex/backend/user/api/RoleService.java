package com.cortex.backend.user.api;

import com.cortex.backend.user.domain.Role;
import com.cortex.backend.infrastructure.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  @Cacheable("roles")
  public List<Role> getAllRoles() {
    return (List<Role>) roleRepository.findAll();
  }

  @Cacheable(value = "roles", key = "#name")
  public Optional<Role> findByName(String name) {
    return roleRepository.findByName(name);
  }

  @CacheEvict(value = "roles", allEntries = true)
  public void saveRole(Role role) {
    roleRepository.save(role);
  }
}