package com.cortex.backend.education.module.internal;

import com.cortex.backend.education.module.domain.ModuleEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends
    CrudRepository<ModuleEntity, Long> {

  Optional<ModuleEntity> findBySlug(String slug);
}
