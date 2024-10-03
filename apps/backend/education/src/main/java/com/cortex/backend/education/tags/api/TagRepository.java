package com.cortex.backend.education.internal;

import com.cortex.backend.core.domain.Tag;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {
  Optional<Tag> findByName(String name);

}
