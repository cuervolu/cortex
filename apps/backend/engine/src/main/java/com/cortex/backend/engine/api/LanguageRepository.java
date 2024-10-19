package com.cortex.backend.engine.api;

import com.cortex.backend.core.domain.Language;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends CrudRepository<Language, Long> {
  Optional<Language> findByName(String name);

  boolean existsByName(String name);
}
