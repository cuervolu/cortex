package com.cortex.backend.infrastructure.persistence;

import com.cortex.backend.user.domain.Role;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
  Optional<Role> findByName(String name);
  List<Role> findAllByNameIn(Collection<String> names);
}